package tests.auth.service

import com.example.auth.hashToken
import com.example.db.RefreshTokenTable
import com.example.db.suspendTransaction
import com.example.exception.TokenGenerationException
import io.mockk.every
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.selectAll
import org.junit.Test
import java.time.Instant
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RefreshTokenIntegrationTest : AuthServiceTest() {
    @Test
    fun `refreshing token should provide a new access token`() = runTest {
        // Arrange - Register a user first to set up database state
        val (user, userTokens) = registerAndGetUserData()

        // Mock Jwt operations
        mockJwtRefreshOperations(user.id, userTokens.refreshToken)

        // Capture time before refreshing
        val beforeRefresh = Instant.now()

        // Act
        val newAccessToken = authService.refreshAccessToken(userTokens.refreshToken)

        // Assert - Just verify a token was returned
        assertNotNull(newAccessToken)
        assertTrue(newAccessToken.startsWith("fake-access-token"))

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
        val (user, userTokens) = registerAndGetUserData()

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
}