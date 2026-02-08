package com.example.routes.auth

import com.example.config.authService
import com.example.domain.LoginData
import com.example.dto.DtoRes
import com.example.dto.LoginRequestDto
import com.example.service.AuthService
import com.example.utils.isValidEmail
import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.login() {
    post("/login") {
        val req = call.receive<LoginRequestDto>()
        val authService: AuthService = call.authService

        // Login and obtain tokens
        val tokens = authService.login(LoginData(req.email, req.password))

        // Send user the tokens data
        call.respond(
            HttpStatusCode.OK,
            DtoRes.success(
                "login successful",
                mapOf(
                    "access_token" to tokens.accessToken,
                    "refresh_token" to tokens.refreshToken
                )
            )
        )
    }
}

fun validateLoginRequest(req: LoginRequestDto): ValidationResult = when {
    !req.email.isValidEmail()
        -> ValidationResult.Invalid("invalid email format")

    req.password.isEmpty() ->
        ValidationResult.Invalid("password cannot be empty")

    else -> ValidationResult.Valid
}