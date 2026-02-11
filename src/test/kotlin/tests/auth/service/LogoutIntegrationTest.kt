package tests.auth.service

import com.example.auth.hashToken
import com.example.db.RefreshTokenTable
import com.example.db.suspendTransaction
import com.example.domain.RegisterData
import com.example.domain.TokenValidationResult
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.selectAll
import org.junit.Test
import java.time.Instant
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LogoutIntegrationTest : AuthServiceTest() {
    @Test
    fun `logout should revoke the refresh token`() = runTest {
        // Arrange
        val (user, userTokens) = registerAndGetUserData()
        val refreshTokenHash = hashToken(userTokens.refreshToken)

        // Verify token is initially valid
        val initialValidation = refreshTokenRepository.validate(refreshTokenHash, user.id)
        assertTrue(initialValidation is TokenValidationResult.Valid)

        // Act
        authService.logout(userTokens.refreshToken)

        // Verify token is now revoked
        val validationAfterLogout = refreshTokenRepository.validate(refreshTokenHash, user.id)
        assertTrue(validationAfterLogout is TokenValidationResult.Revoked)
    }

    @Test
    fun `logout should update revokedAt timestamp`() = runTest {
        // Arrange
        val (_, userTokens) = registerAndGetUserData()
        val refreshTokenHash = hashToken(userTokens.refreshToken)

        // Verify revokedAt is initially null
        val initialRevokedAt = suspendTransaction {
            RefreshTokenTable
                .selectAll()
                .where { RefreshTokenTable.hash eq refreshTokenHash }
                .first()[RefreshTokenTable.revokedAt]
        }
        assertNull(initialRevokedAt)

        val timeBeforeLogout = Instant.now()

        // Act
        authService.logout(userTokens.refreshToken)

        // Assert - Verify revokedAt is now set
        suspendTransaction {
            val revokedAt = RefreshTokenTable
                .selectAll()
                .where { RefreshTokenTable.hash eq refreshTokenHash }
                .first()[RefreshTokenTable.revokedAt]

            assertNotNull(revokedAt)
            assertTrue(revokedAt > timeBeforeLogout)
        }
    }

    @Test
    fun `logout with non-existent token should not throw exception`() = runTest {
        // Arrange
        val nonExistentToken = "non-existent-refresh-token"

        // Act & Assert - Should not throw, just log warning
        authService.logout(nonExistentToken)

        // If we get here without exception, test passes
    }

    @Test
    fun `logout should not affect other user tokens`() = runTest {
        // Arrange - Register two users
        val (user1, userTokens1) = registerAndGetUserData(
            RegisterData("User One", "user1@example.com", "Password223!!")
        )

        val (user2, userTokens2) = registerAndGetUserData(
            RegisterData("User Two", "user2@example.com", "Password456!!")
        )

        val hash1 = hashToken(userTokens1.refreshToken)
        val hash2 = hashToken(userTokens2.refreshToken)

        // Act - Logout user1
        authService.logout(userTokens1.refreshToken)

        // Assert - User1's token is revoked
        val validation1 = refreshTokenRepository.validate(hash1, user1.id)
        assertTrue(validation1 is TokenValidationResult.Revoked)

        // Assert - User2's token is still valid
        val validation2 = refreshTokenRepository.validate(hash2, user2.id)
        assertTrue(validation2 is TokenValidationResult.Valid)
    }

    @Test
    fun `logout called twice on same token should be idempotent`() = runTest {
        // Arrange
        val (user, userTokens) = registerAndGetUserData()
        val refreshTokenHash = hashToken(userTokens.refreshToken)

        // Act - Logout twice
        authService.logout(userTokens.refreshToken)
        authService.logout(userTokens.refreshToken) // Second call

        // Assert - Token is revoked (not in some broken state)
        val validation = refreshTokenRepository.validate(refreshTokenHash, user.id)
        assertTrue(validation is TokenValidationResult.Revoked)

        // Verify revokedAt timestamp (should be from first logout)
        suspendTransaction {
            val revokedAt = RefreshTokenTable
                .selectAll()
                .where { RefreshTokenTable.hash eq refreshTokenHash }
                .first()[RefreshTokenTable.revokedAt]

            assertNotNull(revokedAt)
        }
    }
}