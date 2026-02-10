package com.example.service

import com.example.auth.hashPassword
import com.example.auth.hashToken
import com.example.auth.verifyPassword
import com.example.config.TokenDuration
import com.example.db.suspendTransaction
import com.example.domain.*
import com.example.exception.InvalidCredentialsException
import com.example.exception.UserAlreadyExistsException
import com.example.repository.IRefreshTokenRepository
import com.example.repository.IUserRepository
import java.time.Instant
import java.util.*

class AuthService(
    private val userRepository: IUserRepository,
    private val refreshTokenRepository: IRefreshTokenRepository,
    private val jwtService: JwtService
) {
    suspend fun login(loginData: LoginData): TokenStrings {
        // Find user by email
        val user = userRepository.findByEmail(loginData.email)
            ?: throw InvalidCredentialsException()

        // Verify raw password matches user's password
        if (!verifyPassword(loginData.password, user.passwordHash))
            throw InvalidCredentialsException()

        // Generate tokens
        val tokens = generateTokens(user.toPrincipal())

        // Save refresh token in database
        saveRefreshTokenInDb(user.id, tokens.refreshToken)

        // Return token pair
        return tokens
    }

    suspend fun register(registerData: RegisterData): TokenStrings {
        // Check if user exists
        if (userRepository.findByEmail(registerData.email) != null)
            throw UserAlreadyExistsException()

        // Hash password for secure database storage
        val passwordHash = hashPassword(registerData.password)

        return suspendTransaction {
            // Create user
            val user = userRepository.create(
                UserCreate(
                    registerData.name,
                    registerData.email,
                    passwordHash
                )
            )

            // Generate tokens
            val tokens = generateTokens(user.toPrincipal())

            // Save refresh token in database
            saveRefreshTokenInDb(user.id, tokens.refreshToken)

            // Return token pair
            tokens
        }
    }

    private fun generateTokens(userPrincipal: UserPrincipal): TokenStrings {
        val accessToken = jwtService.generateAccessToken(userPrincipal)
        val refreshToken = jwtService.generateRefreshToken(userPrincipal.id)

        return TokenStrings(accessToken, refreshToken)
    }

    private suspend fun saveRefreshTokenInDb(userId: UUID, refreshToken: String) {
        // Hash the token for secure database storage
        val refreshTokenHash = hashToken(refreshToken)

        // Set refresh token expiration date
        val expiresAt = Instant.now().plus(TokenDuration.REFRESH_TOKEN)

        // Save refresh token in the database
        refreshTokenRepository.save(
            RefreshTokenCreate(
                hash = refreshTokenHash,
                userId = userId,
                expiresAt = expiresAt
            )
        )
    }
}