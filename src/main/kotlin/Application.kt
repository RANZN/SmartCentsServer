package com.ranjan

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ranjan.application.auth.authRoutes
import com.ranjan.application.checkHealth
import com.ranjan.application.update.checkUpdateRoute
import com.ranjan.data.db.DatabaseFactory
import com.ranjan.data.service.JwtConfig
import com.ranjan.di.appModule
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureKoin()
    configureSerialization()
    configureSecurity()
    configureRoutes()
    configureExceptionHandling()
    configureCORS()
}

fun Application.configureKoin() {
    install(Koin) {
        modules(appModule)
    }
}

fun Application.configureRoutes() {
    checkHealth()
    checkUpdateRoute()
    authRoutes()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(JwtConfig.SECRET))
                    .withAudience(JwtConfig.AUDIENCE)
                    .withIssuer(JwtConfig.ISSUER)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(
                text = "500: ${cause.message}",
                status = HttpStatusCode.InternalServerError
            )
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(text = "404: Page Not Found", status = status)
        }

        status(HttpStatusCode.Unauthorized) { call, status ->
            call.respondText(text = "401: Unauthorized", status = status)
        }
    }
}

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost() // In production, you should restrict this
    }
}