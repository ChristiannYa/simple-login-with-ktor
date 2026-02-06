package com.example.plugins

import com.example.dto.DtoRes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.statements.BatchDataInconsistentException

fun Application.configureStatusPages() {
    install(StatusPages) {
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

        exception<BadRequestException> { call, cause ->
            println("> Bad Request Exception")
            println("> Message: ${cause.message}")
            println("> Cause: ${cause.cause}")

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

        exception<IllegalStateException> { call, cause ->
            println("> Illegal State Exception")
            println("> Message: ${cause.message}")
            println("> Cause: ${cause.cause}")

            call.respond(
                HttpStatusCode.InternalServerError,
                DtoRes.error("internal server error")
            )
        }

        exception<BatchDataInconsistentException> { call, cause ->
            println("> Batch Data Inconsistent Exception")
            println("> Message: ${cause.message}")
            println("> Cause: ${cause.cause}")

            call.respond(
                HttpStatusCode.InternalServerError,
                DtoRes.error("internal server error")
            )
        }
    }
}