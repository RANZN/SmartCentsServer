package com.ranjan.domain.usecase

import com.ranjan.domain.model.AuthResponse
import com.ranjan.domain.model.LoginRequest
import com.ranjan.domain.repository.RefreshTokenRepo
import com.ranjan.domain.repository.UserRepository
import com.ranjan.domain.service.TokenProvider
import com.ranjan.domain.service.PasswordCipher

class LoginUserUseCase(
    private val userRepository: UserRepository,
    private val refreshTokenRepo: RefreshTokenRepo,
    private val passwordCipher: PasswordCipher,
    private val tokenProvider: TokenProvider,
) {

    suspend fun execute(loginRequest: LoginRequest): Result<AuthResponse> = runCatching {
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw SecurityException("Invalid email or password")

        val isPasswordCorrect = passwordCipher.verifyPassword(loginRequest.password, user.hashedPassword)

        if (isPasswordCorrect.not()) throw SecurityException("Invalid email or password")

        val token = tokenProvider.createToken(user)
        refreshTokenRepo.save(user.userId.toString(), token.refreshToken)
        AuthResponse(token, user.asResponse())
    }
}