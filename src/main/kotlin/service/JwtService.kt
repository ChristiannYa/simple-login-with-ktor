package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.example.config.JwtConfig
import com.example.config.TokenConfiguration
import com.example.domain.UserPrincipal
import com.example.domain.UserType
import com.example.dto.DtoRes
import com.example.exception.TokenGenerationException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.time.Duration
import java.time.Instant
import java.util.*

class JwtService(application: Application) {
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

    fun generateAccessToken(userPrincipal: UserPrincipal): String {
        val claims = mapOf(
            "userId" to userPrincipal.id.toString(),
            "type" to userPrincipal.type.name,
            "isPremium" to userPrincipal.isPremium.toString()
        )

        return generateJwtToken(
            tokenType = "Access",
            claims = claims,
            duration = TokenConfiguration.ACCESS_TOKEN_DURATION
        )
    }

    fun generateRefreshToken(userId: UUID): String {
        val claims = mapOf("userId" to userId.toString())

        return generateJwtToken(
            tokenType = "Refresh",
            claims = claims,
            duration = TokenConfiguration.REFRESH_TOKEN_DURATION
        )
    }

    private fun generateJwtToken(
        tokenType: String,
        claims: Map<String, String>,
        duration: Duration
    ): String {
        return try {
            JWT
                .create()
                .withAudience(jwtConfig.audience)
                .withIssuer(jwtConfig.issuer)
                .withClaims(claims)
                .withExpiresAt(Date.from(Instant.now().plus(duration)))
                .sign(Algorithm.HMAC256(jwtConfig.secret))
        } catch (ex: IllegalArgumentException) {
            throw TokenGenerationException(tokenType = tokenType, cause = ex)
        } catch (ex: JWTCreationException) {
            throw TokenGenerationException(tokenType = tokenType, cause = ex)
        }
    }

    private fun JWTCreator.Builder.withClaims(
        claims: Map<String, String>
    ): JWTCreator.Builder {
        claims.forEach { (k, v) ->
            this.withClaim(k, v)
        }
        return this
    }
}