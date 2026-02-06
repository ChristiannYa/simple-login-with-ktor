package com.example.routes.auth

import com.example.auth.verifyPassword
import com.example.config.jwtService
import com.example.config.userRepository
import com.example.domain.UserPrincipal
import com.example.dto.DtoRes
import com.example.dto.LoginRequestDto
import com.example.utils.isValidEmail
import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.login() {
    post("/login") {
        val req = call.receive<LoginRequestDto>()
        val userRepository = call.userRepository
        val jwtService = call.jwtService

        // Find user by email
        val user = userRepository.findByEmail(req.email)

        // Handle user not found
        if (user == null) {
            call.respond(
                HttpStatusCode.Unauthorized,
                DtoRes.error("invalid email or password")
            )
            return@post
        }

        // Verify password with error handling
        val isPasswordValid = try {
            verifyPassword(req.password, user.passwordHash)
        } catch (_: Exception) {
            false
        }

        // Handle invalid password
        if (!isPasswordValid) {
            call.respond(
                HttpStatusCode.Unauthorized,
                DtoRes.error("invalid email or password")
            )
            return@post
        }

        // Create UserPrincipal
        val userPrincipal = UserPrincipal(
            id = user.id,
            type = user.type,
            isPremium = user.isPremium
        )

        // Generate Jwt token
        val jwtToken = jwtService.createJwt(userPrincipal)

        // Respond with token
        call.respond(
            HttpStatusCode.OK,
            DtoRes.success(
                "login successful",
                mapOf("jwt_token" to jwtToken)
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