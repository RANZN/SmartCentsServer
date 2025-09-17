package com.ranjan.domain.usecase

import com.ranjan.domain.model.AuthResponse
import com.ranjan.domain.model.LoginRequest
import com.ranjan.domain.service.PasswordCipher
import com.ranjan.domain.service.TokenProvider
import com.ranjan.domain.repository.UserRepository

class LoginUserUseCase(
    private val userRepository: UserRepository,
    private val tokenProvider: TokenProvider,
    private val passwordCipher: PasswordCipher,
) {

    suspend fun execute(loginRequest: LoginRequest): Result<AuthResponse> = runCatching {
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw SecurityException("Invalid email or password")

        val isPasswordCorrect = passwordCipher.verifyPassword(loginRequest.password, user.hashedPassword)

        if (isPasswordCorrect) {
            val token = tokenProvider.createToken(user)
            AuthResponse(token, user.asResponse())
        } else {
            throw SecurityException("Invalid email or password")
        }
    }
}