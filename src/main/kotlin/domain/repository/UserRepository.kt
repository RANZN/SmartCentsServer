package com.ranjan.domain.repository

import com.ranjan.domain.model.User

interface UserRepository {
    suspend fun findByEmail(email: String): User?
    suspend fun isEmailExists(email: String): Boolean
    suspend fun saveUser(user: User): User?
}