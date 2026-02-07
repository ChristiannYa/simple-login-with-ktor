package com.example.service

import com.example.auth.hashToken
import com.example.auth.verifyPassword
import com.example.config.TokenConfiguration
import com.example.domain.RefreshTokenCreate
import com.example.domain.UserPrincipal
import com.example.exception.InvalidCredentialsException
import com.example.repository.IRefreshTokenRepository
import com.example.repository.IUserRepository
import java.time.Instant

class AuthService(
    private val userRepository: IUserRepository,
    private val refreshTokenRepository: IRefreshTokenRepository,
    private val jwtService: JwtService
) {
    suspend fun login(email: String, password: String): Pair<String, String> {
        // Find user by email
        val user = userRepository.findByEmail(email)
            ?: throw InvalidCredentialsException()

        // Verify raw password matches user's password
        if (!verifyPassword(password, user.passwordHash))
            throw InvalidCredentialsException()

        // Generate tokens
        val userPrincipal = UserPrincipal(user.id, user.type, user.isPremium)
        val accessToken = jwtService.generateAccessToken(userPrincipal)
        val refreshToken = jwtService.generateRefreshToken(user.id)

        // Store hashed refresh token
        val hashedToken = hashToken(refreshToken)
        val expiresAt = Instant.now().plus(TokenConfiguration.REFRESH_TOKEN_DURATION)

        // Store data needed to save the refresh token in the database
        val refreshTokenCreateData = RefreshTokenCreate(
            hash = hashedToken,
            userId = user.id,
            expiresAt = expiresAt
        )

        // Save the token in the database
        refreshTokenRepository.save(refreshTokenCreateData)

        // Return both tokens
        return accessToken to refreshToken
    }
}