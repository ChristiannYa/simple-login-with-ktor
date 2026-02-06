package com.example.plugins

import com.example.config.JwtConfigKey
import com.example.config.JwtServiceKey
import com.example.service.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureJwt() {
    val jwtService = JwtService(this)

    attributes.put(JwtConfigKey, jwtService.jwtConfig)
    attributes.put(JwtServiceKey, jwtService)

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtService.jwtConfig.realm
            verifier(jwtService.jwtVerifier)
            validate { credential -> jwtService.validate(credential) }
            challenge { _, _ -> jwtService.challenge(this) }
        }
    }
}