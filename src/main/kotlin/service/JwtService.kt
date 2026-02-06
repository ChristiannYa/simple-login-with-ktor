package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.example.config.JwtConfig
// import com.example.config.UserRepositoryKey
import com.example.domain.UserPrincipal
import com.example.domain.UserType
import com.example.dto.DtoRes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.*

class JwtService(application: Application) {
    // private val userRepository = application.attributes[UserRepositoryKey]

    val jwtConfig = JwtConfig(
        secret = application.environment.config.property("jwt.secret").getString(),
        issuer = application.environment.config.property("jwt.issuer").getString(),
        audience = application.environment.config.property("jwt.audience").getString(),
        realm = application.environment.config.property("jwt.realm").getString()
    )

    val jwtVerifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(jwtConfig.secret))
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.issuer)
        .build()

    fun validate(credential: JWTCredential): UserPrincipal? {
        return try {
            val userId = credential.payload.getClaim("userId").asString()
            val userType = credential.payload.getClaim("type").asString()
            val isPremium = credential.payload.getClaim("isPremium").asBoolean()

            if (userId != null && userType != null && isPremium != null) {
                UserPrincipal(
                    id = UUID.fromString(userId),
                    type = UserType.valueOf(userType),
                    isPremium = isPremium
                )
            } else null
        } catch (_: Exception) { // Triggers the challenge block
            null
        }
    }

    suspend fun challenge(context: JWTChallengeContext) {
        context.call.respond(
            HttpStatusCode.Unauthorized,
            DtoRes.error("token is not valid or has expired")
        )
    }

    fun createJwt(userPrincipal: UserPrincipal): String {
        return try {
            JWT.create()
                .withAudience(jwtConfig.audience)
                .withIssuer(jwtConfig.issuer)
                .withClaim("userId", userPrincipal.id.toString())
                .withClaim("type", userPrincipal.type.name)
                .withClaim("isPremium", userPrincipal.isPremium)
                .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 60 * 24))
                .sign(Algorithm.HMAC256(jwtConfig.secret))
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid JWT claim for user ${userPrincipal.id}: ${ex.message}",
                ex
            )
        } catch (ex: JWTCreationException) {
            throw JWTCreationException(
                "Failed to sign JWT for user ${userPrincipal.id}: ${ex.message}",
                ex
            )
        }
    }
}