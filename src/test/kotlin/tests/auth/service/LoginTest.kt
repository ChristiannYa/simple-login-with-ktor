package tests.auth.service

import com.example.auth.hashPassword
import com.example.auth.hashToken
import com.example.auth.verifyPassword
import com.example.domain.LoginData
import com.example.domain.User
import com.example.domain.UserType
import com.example.exception.InvalidCredentialsException
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LoginTest : AuthServiceTest() {
    @Test
    fun `login should return access and refresh tokens when credentials are valid`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val passwordHash = hashPassword(password)
        val userId = UUID.randomUUID()

        val user = User(
            id = userId,
            name = "Test User",
            email = email,
            passwordHash = passwordHash,
            type = UserType.USER,
            isPremium = false,
            createdAt = Instant.now()
        )

        val loginData = LoginData(email, password)

        val expectedAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.access"
        val expectedRefreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh"

        // Define what mocks should return when called
        coEvery { userRepository.findByEmail(email) } returns user
        every { verifyPassword(password, passwordHash) } returns true
        every { jwtService.generateAccessToken(any()) } returns expectedAccessToken
        every { jwtService.generateRefreshToken(any()) } returns expectedRefreshToken
        every { hashToken(expectedRefreshToken) } returns "hashed_refresh_token"
        coEvery { refreshTokenRepository.save(any()) } returns mockk(relaxed = true)

        // Act
        val (accessToken, refreshToken) = authService.login(loginData)

        // Assert
        assertEquals(expectedAccessToken, accessToken)
        assertEquals(expectedRefreshToken, refreshToken)

        // Verify interactions
        coVerify(exactly = 1) { userRepository.findByEmail(email) }
        verify(exactly = 1) { verifyPassword(password, passwordHash) }
        verify(exactly = 1) {
            jwtService.generateAccessToken(
                match {
                    it.id == userId &&
                            it.type == UserType.USER &&
                            !it.isPremium
                }
            )
        }
        verify(exactly = 1) { jwtService.generateRefreshToken(userId) }
        verify(exactly = 1) { hashToken(expectedRefreshToken) }
        coVerify(exactly = 1) {
            refreshTokenRepository.save(
                match {
                    it.hash == "hashed_refresh_token" &&
                            it.userId == userId
                }
            )
        }
    }

    @Test
    fun `login should throw InvalidCredentialsException when password is incorrect`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "wrongpassword"
        val correctPasswordHash = hashPassword("correctpassword")
        val userId = UUID.randomUUID()

        val user = User(
            id = userId,
            name = "Test User",
            email = email,
            passwordHash = correctPasswordHash,
            type = UserType.USER,
            isPremium = false,
            createdAt = Instant.now()
        )

        val loginData = LoginData(email, password)

        coEvery { userRepository.findByEmail(email) } returns user
        every { verifyPassword(password, correctPasswordHash) } returns false

        // Act & Assert
        assertFailsWith<InvalidCredentialsException> {
            authService.login(loginData)
        }

        coVerify(exactly = 1) { userRepository.findByEmail(email) }
        verify(exactly = 1) { verifyPassword(password, correctPasswordHash) }
        verify(exactly = 0) { jwtService.generateAccessToken(any()) }
        verify(exactly = 0) { jwtService.generateRefreshToken(any()) }
    }
}