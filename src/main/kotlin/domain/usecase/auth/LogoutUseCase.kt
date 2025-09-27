package com.ranjan.domain.usecase.auth

import com.ranjan.domain.repository.RefreshTokenRepo

class LogoutUseCase(
    private val refreshTokenRepo: RefreshTokenRepo,
) {

    suspend fun execute(token: String): Result<Unit> = runCatching {
        val rowsDeleted = refreshTokenRepo.deleteByToken(token)
        if (rowsDeleted == 0) throw SecurityException("Invalid token")
    }
}