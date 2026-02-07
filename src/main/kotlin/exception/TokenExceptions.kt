package com.example.exception

/**
 * Base exception for token-related errors
 */
open class TokenException(
    tokenType: String? = null,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Thrown when a JWT token is invalid, expires, or revoked
 */
class InvalidTokenException(
    tokenType: String,
    message: String = "$tokenType Token is invalid or has expired"
) : TokenException(tokenType, message)

/**
 * Thrown when a refresh token has been revoked
 */
class RevokedRefreshTokenException(
    message: String = "Refresh token has been revoked"
) : TokenException(message = message)

/**
 * Thrown when token generation fails
 */
class TokenGenerationException(
    tokenType: String,
    message: String = "Failed to create $tokenType Token",
    cause: Throwable? = null
) : TokenException(tokenType, message, cause)