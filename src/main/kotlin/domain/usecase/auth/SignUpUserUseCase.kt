package com.ranjan.domain.usecase.auth

import com.ranjan.domain.model.AuthResponse
import com.ranjan.domain.model.SignupRequest
import com.ranjan.domain.model.User
import com.ranjan.domain.repository.UserRepository
import com.ranjan.domain.service.TokenProvider
import com.ranjan.domain.service.PasswordCipher
import java.util.UUID

class SignUpUserUseCase(
    private val userRepository: UserRepository,
    private val tokenProvider: TokenProvider,
    private val passwordCipher: PasswordCipher,
) {

    suspend fun execute(signUpRequest: SignupRequest): Result<AuthResponse> = runCatching {
        if (!signUpRequest.email.contains("@") || signUpRequest.password.length < 8) {
            throw IllegalArgumentException("Invalid email or password must be at least 8 characters.")
        }

        if (userRepository.isEmailExists(signUpRequest.email)) {
            throw IllegalStateException("User already exists with this email")
        }

        val hashedPassword = passwordCipher.hashPassword(signUpRequest.password)

        val newUser = User(
            userId = UUID.randomUUID(), // Generate the ID here
            name = signUpRequest.name,
            email = signUpRequest.email,
            hashedPassword = hashedPassword
        )

        val savedUser = userRepository.saveUser(newUser) ?: throw Exception("Failed to create account")

        val token = tokenProvider.createToken(savedUser)
        AuthResponse(token, user = savedUser.asResponse())
    }

}