package com.example.domain

import com.example.exception.InvalidTokenException
import java.time.Instant
import java.util.*

data class RefreshToken(
    val id: UUID,
    val userId: UUID,
    val hash: String,
    val expiresAt: Instant,
    val createdAt: Instant,
    val lastUsedAt: Instant? = null,
    val revokedAt: Instant? = null
) {
    fun isExpired(): Boolean = expiresAt.isBefore(Instant.now())
    fun isRevoked(): Boolean = revokedAt != null
}

data class RefreshTokenCreate(
    val hash: String,
    val userId: UUID,
    val expiresAt: Instant
)

data class TokenStrings(
    val accessToken: String,
    val refreshToken: String
)

data class LoginData(
    val email: String,
    val password: String
)

data class RegisterData(
    val name: String,
    val email: String,
    val password: String
)

sealed class TokenValidationResult {
    data class Valid(val token: RefreshToken) : TokenValidationResult()
    object NotFound : TokenValidationResult()
    object Revoked : TokenValidationResult()
    object Expired : TokenValidationResult()
}

fun TokenValidationResult.getTokenOrThrow(): RefreshToken = when (this) {
    is TokenValidationResult.NotFound ->
        throw InvalidTokenException("Refresh Token not found")

    is TokenValidationResult.Revoked ->
        throw InvalidTokenException("Refresh Token revoked")

    is TokenValidationResult.Expired ->
        throw InvalidTokenException("Refresh token expired")

    is TokenValidationResult.Valid -> this.token
}

enum class TokenType { ACCESS, REFRESH }