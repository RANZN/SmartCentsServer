package com.ranjan.di

import com.ranjan.application.auth.AuthController
import com.ranjan.data.service.JwtTokenProvider
import com.ranjan.data.service.PasswordCipherImpl
import com.ranjan.data.repository.UserRepositoryImpl
import com.ranjan.domain.service.PasswordCipher
import com.ranjan.domain.service.TokenProvider
import com.ranjan.domain.repository.UserRepository
import com.ranjan.domain.usecase.LoginUserUseCase
import com.ranjan.domain.usecase.SignUpUserUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val appModule = module {
    singleOf(::AuthController)
    singleOf(::LoginUserUseCase)
    singleOf(::SignUpUserUseCase)
    singleOf<PasswordCipher>(::PasswordCipherImpl)
    singleOf<UserRepository>(::UserRepositoryImpl)

    single<TokenProvider> {
        val secret = "your-super-secret-for-jwt"
        val issuer = "your-issuer"
        val audience = "your-audience"
        JwtTokenProvider(secret, issuer, audience)
    }

}