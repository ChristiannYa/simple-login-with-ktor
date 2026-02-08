package com.example.domain

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
)

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