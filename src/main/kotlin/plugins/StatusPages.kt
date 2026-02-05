package com.example.plugins

import com.example.dto.DtoRes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            println("bad request exception cause: $cause")
            call.respond(
                HttpStatusCode.BadRequest,
                DtoRes.error("invalid request body")
            )
        }

        exception<RequestValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                DtoRes.error(cause.reasons.joinToString(", "))
            )
        }

        status(HttpStatusCode.Unauthorized) { call, code ->
            call.respond(code, DtoRes.error("unauthorized"))
        }

        status(HttpStatusCode.NotFound) { call, code ->
            call.respond(code, DtoRes.error("404 not found"))
        }

        // Can catch "missing Content-Type header"
        status(HttpStatusCode.UnsupportedMediaType) { call, code ->
            call.respond(code, DtoRes.error("unsupported media type"))
        }
    }
}