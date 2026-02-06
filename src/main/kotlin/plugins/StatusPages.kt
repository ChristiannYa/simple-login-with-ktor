package com.example.plugins

import com.auth0.jwt.exceptions.JWTCreationException
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
            cause.logDetails()

            call.respond(
                HttpStatusCode.BadRequest,
                DtoRes.error("invalid request body")
            )
        }

        exception<RequestValidationException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.BadRequest,
                DtoRes.error(cause.reasons.joinToString(", "))
            )
        }

        exception<IllegalStateException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.InternalServerError,
                DtoRes.error("internal server error")
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.InternalServerError,
                DtoRes.error("internal server error")
            )
        }

        exception<JWTCreationException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.InternalServerError,
                DtoRes.error("internal server error")
            )
        }

        exception<BatchDataInconsistentException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.InternalServerError,
                DtoRes.error("internal server error")
            )
        }
    }
}

private fun Throwable.logDetails() {
    val exceptionName = this::class.simpleName ?: "Unknown Exception"

    println("> $exceptionName")
    println("> Message: ${this.message}")
    println("> Cause: ${this.cause}")

    this.printStackTrace()
}