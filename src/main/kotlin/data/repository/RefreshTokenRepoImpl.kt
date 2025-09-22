package com.ranjan.data.repository

import com.ranjan.data.db.DatabaseFactory.dbQuery
import com.ranjan.data.db.RefreshTokens
import com.ranjan.data.service.JwtConfig
import com.ranjan.domain.model.RefreshTokenEntity
import com.ranjan.domain.repository.RefreshTokenRepo
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class RefreshTokenRepoImpl : RefreshTokenRepo {

    override suspend fun save(userId: String, refreshToken: String): RefreshTokenEntity? = dbQuery {
        val expiry = Clock.System.now().plus(JwtConfig.Lifetime.refresh)
        val insertStatement = RefreshTokens.insert {
            it[this.userId] = userId
            it[this.token] = refreshToken
            it[this.expiresAt] = expiry
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::toRefreshTokenEntity)
    }

    override suspend fun findByToken(token: String): Boolean {
        return RefreshTokens.selectAll().where {
            RefreshTokens.token eq token
        }.empty().not()
    }

    override suspend fun deleteByUserId(userId: String) {
        transaction {
            RefreshTokens.deleteWhere { this.userId eq userId }
        }
    }

    override suspend fun deleteByToken(token: String): Int {
        return transaction {
            RefreshTokens.deleteWhere { this.token eq token }
        }
    }

    private fun toRefreshTokenEntity(row: ResultRow): RefreshTokenEntity {
        return RefreshTokenEntity(
            id = row[RefreshTokens.id].toString(),
            userId = row[RefreshTokens.userId],
            token = row[RefreshTokens.token],
            expiresAt = row[RefreshTokens.expiresAt],
            createdAt = row[RefreshTokens.createdAt]
        )
    }
}