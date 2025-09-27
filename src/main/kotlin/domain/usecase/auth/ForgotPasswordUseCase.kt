package com.ranjan.domain.usecase.auth

import com.ranjan.domain.repository.UserRepository

class ForgotPasswordUseCase(
    private val userRepository: UserRepository,
) {

    suspend fun execute(email: String) = runCatching {
        userRepository.findByEmail(email) ?: throw SecurityException("User Not Found")
    }
}