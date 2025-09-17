package com.ranjan.domain.service

interface PasswordCipher {
    suspend fun hashPassword(password: String): String
    suspend fun verifyPassword(password: String, hashedPassword: String): Boolean
}