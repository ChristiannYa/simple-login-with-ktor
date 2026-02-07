package com.example.exception

/**
 * Base exception for authentication-related errors
 */
open class AuthException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Thrown when login credentials are invalid
 */
class InvalidCredentialsException(
    message: String = "Invalid email or password"
) : AuthException(message)

/**
 * Thrown when a user tries to access a resource without proper authentication
 */
class UnauthorizedException(
    message: String = "Authentication required"
) : AuthException(message)

/**
 * Thrown when a user doesn't have permission to access a resource
 */
class ForbiddenException(
    message: String = "You don't have permission to access this resource"
) : AuthException(message)