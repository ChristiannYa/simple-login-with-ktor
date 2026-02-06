package com.example.routes.auth

import com.example.auth.hashPassword
import com.example.config.userRepository
import com.example.domain.UserCreate
import com.example.dto.DtoRes
import com.example.dto.RegisterRequestDto
import com.example.dto.toDto
import com.example.utils.*
import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.register() {
    post("/register") {
        val req = call.receive<RegisterRequestDto>()
        val userRepository = call.userRepository

        // Check if user already exists
        val userByEmail = userRepository.findByEmail(req.email)

        // Show error if email already exists
        if (userByEmail != null) {
            call.respond(
                HttpStatusCode.Conflict,
                DtoRes.error("this email is already in use")
            )
            return@post
        }

        // @TODO: Implement a transaction in order to do the following:
        //        - 1. Create user
        //        - 2. Create access token
        //        - 3. Create refresh token

        // Hash password for secure insertion
        val hashedPassword = hashPassword(req.password)

        // Create user
        val userToCreate = UserCreate(req.name, req.email, hashedPassword)
        val userCreated = userRepository.createUser(userToCreate)

        // Respond with user
        call.respond(
            HttpStatusCode.Created,
            DtoRes.success(
                "user created successfully",
                mapOf("user_created" to userCreated.toDto())
            )
        )
    }
}

fun validateRegisterRequest(req: RegisterRequestDto): ValidationResult {
    validateName(req.name).toValidationResult()
        .let { if (it is ValidationResult.Invalid) return it }

    if (!req.email.isValidEmail())
        return ValidationResult.Invalid("invalid email")

    validatePassword(req.password).toValidationResult()
        .let { if (it is ValidationResult.Invalid) return it }

    req.confirmedPassword.validate("confirmed password") {
        it.isEqualTo(req.password, "password")
    }.toValidationResult().let { if (it is ValidationResult.Invalid) return it }

    return ValidationResult.Valid
}