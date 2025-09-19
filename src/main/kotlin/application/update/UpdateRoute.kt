package com.ranjan.application.update

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Application.checkUpdateRoute() {
    routing {
        route("/checkUpdate") {
            get {
                call.respond(
                    HttpStatusCode.OK,
                    false
                )
            }
        }
    }
}