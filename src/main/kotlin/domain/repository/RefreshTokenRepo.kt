package com.ranjan.domain.repository

import com.ranjan.domain.model.RefreshTokenEntity

interface RefreshTokenRepo {
    suspend fun save(userId: String, refreshToken: String) : RefreshTokenEntity?
    suspend fun findByToken(token: String): Boolean
    suspend fun deleteByUserId(userId: String)
    suspend fun deleteByToken(token: String) : Int
}