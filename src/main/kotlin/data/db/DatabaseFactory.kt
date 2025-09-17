package com.ranjan.data.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

object DbConfig {
    val DRIVER: String = System.getenv("DB_DRIVER") ?: "org.h2.Driver"
    val URL: String = System.getenv("DB_URL") ?: "jdbc:h2:file:./build/db"
    val USER: String = System.getenv("DB_USER") ?: "root"
    val PASSWORD: String = System.getenv("DB_PASSWORD") ?: ""
}

object DatabaseFactory {
    fun init() {
        val driverClassName = DbConfig.DRIVER
        var jdbcURL = DbConfig.URL
        var user = DbConfig.USER
        var dbPassword = DbConfig.PASSWORD

        // This logic correctly handles converting Render's URL to the JDBC format.
        // It now checks if the URL is the default H2 URL.
        if (jdbcURL.startsWith("jdbc:h2:").not()) {
            val dbUri = URI(jdbcURL)
            val userInfo = dbUri.userInfo.split(":")
            user = userInfo[0]
            dbPassword = userInfo[1]
            jdbcURL = "jdbc:postgresql://${dbUri.host}:${dbUri.port}${dbUri.path}"
        }

        // Connect to the database using the final, correct credentials
        Database.connect(createHikariDataSource(jdbcURL, driverClassName, user, dbPassword))

        // Create database tables
        transaction {
            SchemaUtils.create(UserTable)
        }
    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        user: String,
        password: String
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        username = user
        this.password = password
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    /**
     * This is the helper function you asked about.
     * It runs the database query block on a dedicated IO thread pool.
     */
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}