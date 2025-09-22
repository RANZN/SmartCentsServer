package com.ranjan.domain.model

import com.ranjan.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val name: String,
    val email: String,
    val hashedPassword: String,
) {
    fun asResponse() = UserResponse(
        userId = userId.toString(),
        name = name,
        email = email
    )
}

@Serializable
data class UserResponse(
    val userId: String,
    val name: String,
    val email: String,
)