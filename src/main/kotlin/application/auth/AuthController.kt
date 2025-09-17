package com.ranjan.application.auth

import com.ranjan.domain.model.ErrorResponse
import com.ranjan.domain.model.LoginRequest
import com.ranjan.domain.model.SignupRequest
import com.ranjan.domain.usecase.LoginUserUseCase
import com.ranjan.domain.usecase.SignUpUserUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class AuthController(
    private val loginUserUseCase: LoginUserUseCase,
    private val signupUserUseCase: SignUpUserUseCase,
) {

    suspend fun login(call: ApplicationCall) {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (_: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request format.")
            )
            return
        }

        val result = loginUserUseCase.execute(loginRequest)

        result.onSuccess { authResponse ->
            call.respond(HttpStatusCode.OK, authResponse)
        }
        result.onFailure { exception ->
            when (exception) {
                is SecurityException -> {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse(exception.message ?: "Invalid credentials")
                    )
                }

                else -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("An internal server error occurred.")
                    )
                }
            }
        }
    }

    suspend fun signup(call: ApplicationCall) {
        val signUpRequest = try {
            call.receive<SignupRequest>()
        } catch (_: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Invalid request format.")
            )
            return
        }

        val result = signupUserUseCase.execute(signUpRequest)

        result.onSuccess { authResponse ->
            call.respond(HttpStatusCode.Created, authResponse)
        }

        result.onFailure { exception ->
            when (exception) {
                is SecurityException -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(exception.message ?: "Invalid credentials")
                    )
                }

                is IllegalStateException -> {
                    call.respond(
                        HttpStatusCode.Conflict,
                        ErrorResponse(exception.message ?: "This resource already exists.")
                    )
                }

                else -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("An internal server error occurred.")
                    )
                }
            }
        }
    }
}