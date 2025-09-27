package com.ranjan.data.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ranjan.domain.model.AuthToken
import com.ranjan.domain.model.User
import com.ranjan.domain.service.TokenProvider
import java.util.Date
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

object JwtConfig {
    const val NAME = "auth-jwt"
    const val SECRET = "your-super-secret-for-jwt"
    const val ISSUER = "your=issuer"
    const val AUDIENCE = "your-audience"

    object Claims {
        const val USER_ID = "userId"
        const val EMAIL = "email"
        const val NAME = "name"
    }

    object Lifetime {
        val access = 15.minutes
        val refresh = 7.days
    }
}

class JwtTokenProvider : TokenProvider {

    override fun createToken(user: User): AuthToken {
        val accessToken = generateAccessToken(user)
        val refreshToken = generateRefreshToken(user.userId.toString())

        return AuthToken(accessToken, refreshToken)
    }

    private fun generateAccessToken(user: User): String {
        val validity = Date(System.currentTimeMillis() + JwtConfig.Lifetime.access.inWholeMilliseconds)
        return JWT.create()
            .withIssuer(JwtConfig.ISSUER)
            .withAudience(JwtConfig.ISSUER)
            .withClaim(JwtConfig.Claims.USER_ID, user.userId.toString())
            .withClaim(JwtConfig.Claims.EMAIL, user.email)
            .withClaim(JwtConfig.Claims.NAME, user.name)
            .withExpiresAt(validity)
            .sign(Algorithm.HMAC256(JwtConfig.SECRET))
    }

    private fun generateRefreshToken(userId: String): String {
        val validity = Date(System.currentTimeMillis() + JwtConfig.Lifetime.refresh.inWholeMilliseconds)
        return JWT.create()
            .withIssuer(JwtConfig.ISSUER)
            .withAudience(JwtConfig.ISSUER)
            .withSubject(userId)
            .withExpiresAt(validity)
            .sign(Algorithm.HMAC256(JwtConfig.SECRET))
    }

}