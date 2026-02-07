package com.example.plugins

import com.example.service.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureJwt(jwtService: JwtService) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtService.jwtConfig.realm
            verifier(jwtService.jwtVerifier)
            validate { credential -> jwtService.validate(credential) }
            challenge { _, _ -> jwtService.challenge(this) }
        }
    }
}