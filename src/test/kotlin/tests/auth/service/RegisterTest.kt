package tests.auth.service

import com.example.auth.hashPassword
import com.example.auth.hashToken
import com.example.domain.RegisterData
import com.example.domain.User
import com.example.domain.UserType
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class RegisterTest : AuthServiceTest() {
    @Test
    fun `register should return access and refresh tokens when credentials are valid`() = runTest {
        val password = "password123"
        val passwordHash = "mocked_password_hash_12345"

        // Arrange
        val user = User(
            id = UUID.randomUUID(),
            name = "Test User",
            email = "test@example.com",
            passwordHash = passwordHash,
            type = UserType.USER,
            isPremium = false,
            createdAt = Instant.now()
        )

        val registerData = RegisterData(user.name, user.email, password)

        val expectedAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.access"
        val expectedRefreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh"

        // Define mocked returns
        coEvery { userRepository.findByEmail(user.email) } returns null
        every { hashPassword(password) } returns passwordHash
        coEvery { userRepository.create(any()) } returns user
        every { jwtService.generateAccessToken(any()) } returns expectedAccessToken
        every { jwtService.generateRefreshToken(any()) } returns expectedRefreshToken
        every { hashToken(expectedRefreshToken) } returns "hashed_refresh_token"
        coEvery { refreshTokenRepository.save(any()) } returns mockk(relaxed = true)

        // Act
        val (accessToken, refreshToken) = authService.register(registerData)

        // Assert
        assertEquals(expectedAccessToken, accessToken)
        assertEquals(expectedRefreshToken, refreshToken)

        // Verify interactions
        coVerify(exactly = 1) { userRepository.findByEmail(user.email) }
        verify(exactly = 1) { hashPassword(password) }
        coVerify(exactly = 1) {
            userRepository.create(
                match {
                    it.name == user.name &&
                            it.email == user.email &&
                            it.passwordHash == user.passwordHash
                }
            )
        }
        verify(exactly = 1) {
            jwtService.generateAccessToken(
                match {
                    it.id == user.id &&
                            it.type == UserType.USER &&
                            !it.isPremium
                }
            )
        }
        verify(exactly = 1) { jwtService.generateRefreshToken(user.id) }
        verify(exactly = 1) { hashToken(expectedRefreshToken) }
        coVerify(exactly = 1) {
            refreshTokenRepository.save(
                match {
                    it.hash == "hashed_refresh_token" &&
                            it.userId == user.id
                }
            )
        }
    }
}