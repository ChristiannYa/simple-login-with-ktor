package tests.auth.service

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.auth.hashToken
import com.example.db.RefreshTokenTable
import com.example.db.suspendTransaction
import com.example.domain.RegisterData
import com.example.domain.TokenStrings
import com.example.domain.User
import com.example.exception.TokenGenerationException
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.selectAll
import org.junit.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RefreshTokenIntegrationTest : AuthServiceTest() {
    @Test
    fun `refreshing token should provide a new access token`() = runTest {
        // Arrange - Register a user first to set up database state
        val (user, userTokens) = getUserRegisteredData()

        // Mock Jwt operations
        mockJwtRefreshOperations(user.id, userTokens.refreshToken)

        // Capture time before refreshing
        val beforeRefresh = Instant.now()

        // Act
        val newAccessToken = authService.refreshAccessToken(userTokens.refreshToken)

        // Assert
        assertEquals("fake-access-token", newAccessToken)

        // Verify lastUsedAt was updated correctly
        suspendTransaction {
            val refreshTokenRow = RefreshTokenTable
                .selectAll()
                .where { RefreshTokenTable.hash eq hashToken(userTokens.refreshToken) }
                .first()

            assertNotNull(refreshTokenRow[RefreshTokenTable.lastUsedAt])
            assertTrue(refreshTokenRow[RefreshTokenTable.lastUsedAt]!! > beforeRefresh)
        }
    }

    @Test
    fun `lastUsedAt should be updated even if token generation fails`() = runTest {
        // Arrange
        val (user, userTokens) = getUserRegisteredData()

        // Mock Jwt operations
        mockJwtRefreshOperations(user.id, userTokens.refreshToken)

        // Hash refresh token
        val refreshTokenHash = hashToken(userTokens.refreshToken)

        // Use the token to set an initial lastUsedAt
        authService.refreshAccessToken(userTokens.refreshToken)

        // Get lastUsedAt value from the refresh
        val initialLastUsedAt = suspendTransaction {
            RefreshTokenTable
                .selectAll()
                .where { RefreshTokenTable.hash eq refreshTokenHash }
                .first()[RefreshTokenTable.lastUsedAt]
        }.also { assertNotNull(it) }

        // Make token generation fail for the second attempt
        every { jwtService.generateAccessToken(any()) } throws TokenGenerationException("Error generating Access Token")

        // Act & Assert
        assertFailsWith<TokenGenerationException> {
            authService.refreshAccessToken(userTokens.refreshToken)
        }

        // Verify lastUsedAt was still updated despite the failure
        suspendTransaction {
            val updatedLastUsedAt = RefreshTokenTable
                .selectAll()
                .where { RefreshTokenTable.hash eq refreshTokenHash }
                .first()[RefreshTokenTable.lastUsedAt]

            assertNotNull(updatedLastUsedAt)
            assertTrue(updatedLastUsedAt > initialLastUsedAt)
        }
    }

    private suspend fun getUserRegisteredData(): Pair<User, TokenStrings> {
        val registerData = RegisterData("Test User", "test@example.com", "Password123!!")
        val tokens = authService.register(registerData)

        return userRepository.findByEmail(registerData.email)!! to tokens
    }

    private fun mockJwtRefreshOperations(userId: UUID, refreshTokenString: String) {
        mockk<DecodedJWT>().let { mockDecodedJwt ->
            every { mockDecodedJwt.getClaim("userId") } returns mockk {
                every { asString() } returns userId.toString()
            }

            every { jwtService.verifyToken(refreshTokenString, any()) } returns mockDecodedJwt
            every { jwtService.extractUserId(mockDecodedJwt, any()) } returns userId
        }
    }
}