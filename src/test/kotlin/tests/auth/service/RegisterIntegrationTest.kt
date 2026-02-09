package tests.auth.service

import TestDatabase
import com.example.db.RefreshTokenTable
import com.example.db.UserTable
import com.example.db.suspendTransaction
import com.example.domain.RegisterData
import com.example.domain.UserType
import com.example.exception.UserAlreadyExistsException
import com.example.repository.RefreshTokenRepository
import com.example.repository.UserRepository
import com.example.service.AuthService
import com.example.service.JwtService
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class RegisterIntegrationTest {
    private lateinit var database: Database

    private lateinit var userRepository: UserRepository
    private lateinit var refreshTokenRepository: RefreshTokenRepository
    private lateinit var jwtService: JwtService
    private lateinit var authService: AuthService

    companion object {
        @JvmStatic
        @AfterClass
        fun shutdownContainer() {
            TestDatabase.shutDown()
        }
    }

    @Before
    fun setUp() {
        database = TestDatabase.setUp()

        userRepository = UserRepository()
        refreshTokenRepository = RefreshTokenRepository()

        // Mock JwtService to return fake tokens
        jwtService = mockk<JwtService>()
        every { jwtService.generateAccessToken(any()) } returns "fake-access-token"
        every { jwtService.generateRefreshToken(any()) } returns "fake-refresh-token"

        authService = AuthService(userRepository, refreshTokenRepository, jwtService)
    }

    @After
    fun tearDown() {
        TestDatabase.tearDown()
    }

    @Test
    fun `register should create user and refresh token in transaction`() = runTest {
        // Arrange
        val registerData = RegisterData("Test User", "test@example.com", "password123")

        // Act
        val tokens = authService.register(registerData)

        // Assert
        assertNotNull(tokens.accessToken)
        assertNotNull(tokens.refreshToken)
        assertEquals("fake-access-token", tokens.accessToken)
        assertEquals("fake-refresh-token", tokens.refreshToken)

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