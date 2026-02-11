package com.example.plugins

import com.auth0.jwt.exceptions.JWTCreationException
import com.example.dto.DtoRes
import com.example.exception.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.statements.BatchDataInconsistentException

fun Application.configureStatusPages() {
    install(StatusPages) {
        // ------------
        // STATUS CODES
        // ------------
        status(HttpStatusCode.NotFound) { call, code ->
            call.respond(code, DtoRes.error("404 not found"))
        }

        status(HttpStatusCode.UnsupportedMediaType) { call, code ->
            call.respond(code, DtoRes.error("unsupported media type"))
        }

        // ------------------
        // DEFAULT EXCEPTIONS
        // ------------------
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

        // -----------------
        // TOKEN EXCEPTIONS
        // ----------------
        exception<InvalidTokenException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.InternalServerError,
                DtoRes.error("token is invalid")
            )
        }

        exception<TokenVerificationException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.InternalServerError,
                DtoRes.error("token verification failed. ${cause.cause?.message}")
            )
        }

        exception<TokenGenerationException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.InternalServerError,
                DtoRes.error("token generation error")
            )
        }

        // ---------------
        // AUTH EXCEPTIONS
        // ---------------
        exception<InvalidCredentialsException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.Unauthorized,
                DtoRes.error("invalid credentials")
            )
        }

        // ---------------
        // USER EXCEPTIONS
        // ---------------
        exception<UserNotFoundException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.Conflict,
                DtoRes.error("user not found")
            )
        }

        exception<UserAlreadyExistsException> { call, cause ->
            cause.logDetails()

            call.respond(
                HttpStatusCode.Conflict,
                DtoRes.error("user already exists")
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