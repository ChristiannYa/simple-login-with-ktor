package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.JwtConfig
import com.example.config.JwtConfigKey
import com.example.domain.UserPrincipal
import com.example.domain.UserType
import com.example.dto.DtoRes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.*

fun Application.configureJwt() {
    val jwtConfig = JwtConfig(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        realm = environment.config.property("jwt.realm").getString()
    )

    // Store jwtConfig in application attributes for easy access
    attributes.put(JwtConfigKey, jwtConfig)

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )
            validate { credential ->
                try {
                    // Extract claims from the JWT payload
                    val userId = credential.payload.getClaim("userId").asString()
                    val userType = credential.payload.getClaim("type").asString()
                    val isPremium = credential.payload.getClaim("isPremium").asBoolean()
                    val claimsAreValid = userId != null && userType != null && isPremium != null

                    if (claimsAreValid) {
                        UserPrincipal(
                            id = UUID.fromString(userId),
                            type = UserType.valueOf(userType),
                            isPremium = isPremium
                        )
                    } else null
                } catch (_: Exception) {
                    null // This triggers the challenge block
                }
            }
            challenge { _, _ -> // defaultScheme, realm
                call.respond(
                    HttpStatusCode.Unauthorized,
                    DtoRes.error("Token is not valid or has expired")
                )
            }
        }
    }
}