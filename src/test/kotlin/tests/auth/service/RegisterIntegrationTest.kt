package tests.auth.service

import com.example.db.RefreshTokenTable
import com.example.db.UserTable
import com.example.db.suspendTransaction
import com.example.domain.RegisterData
import com.example.domain.UserType
import com.example.exception.UserAlreadyExistsException
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.selectAll
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RegisterIntegrationTest : AuthServiceTest() {
    @Test
    fun `register should create user and refresh token in transaction`() = runTest {
        // Arrange
        val registerData = RegisterData("Test User", "test@example.com", "password123")

        // Act
        val tokens = authService.register(registerData)

        // Assert
        assertNotNull(tokens.accessToken)
        assertNotNull(tokens.refreshToken)
        assertTrue(tokens.accessToken.startsWith("fake-access-token"))
        assertTrue(tokens.refreshToken.startsWith("fake-refresh-token"))

        // Verify user exists in the database
        val user = userRepository.findByEmail(registerData.email)
        assertNotNull(user)
        assertEquals(registerData.name, user.name)
        assertEquals(registerData.email, user.email)
        assertEquals(UserType.USER, user.type)

        // Verify refresh token exists in database
        suspendTransaction {
            val refreshTokenCount = RefreshTokenTable
                .selectAll()
                .where { RefreshTokenTable.userId eq user.id }
                .count()

            assertEquals(1, refreshTokenCount)
        }
    }

    @Test
    fun `register should rollback when a database constraint is violated`() = runTest {
        // Arrange
        val registerData = RegisterData("Test User", "test@example.com", "password123")

        // First registration succeeds
        authService.register(registerData)

        // Try to register with the same email again
        assertFailsWith<UserAlreadyExistsException> {
            authService.register(registerData)
        }

        // Verify only ONE user exists (second attempt didn't create duplicate)
        suspendTransaction {
            val userCount = UserTable
                .selectAll()
                .where { UserTable.email eq registerData.email }
                .count()

            assertEquals(1, userCount)
        }
    }
}