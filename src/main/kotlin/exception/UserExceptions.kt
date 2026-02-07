package com.example.exception

/**
 * Base exception for user-related errors
 */
open class UserException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Thrown when a user is not found
 */
class UserNotFoundException(
    message: String = "User not found"
) : UserException(message)

/**
 * Thrown when trying to create a user that already exists
 */
class UserAlreadyExistsException(
    message: String = "User with this email already exists"
) : UserException(message)

/**
 * Thrown when user validation fails
 */
class InvalidUserDataException(
    message: String = "Invalid user data"
) : UserException(message)