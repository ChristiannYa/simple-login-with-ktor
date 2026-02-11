package tests.auth.service

import TestDatabase
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.domain.RegisterData
import com.example.domain.TokenStrings
import com.example.domain.User
import com.example.repository.RefreshTokenRepository
import com.example.repository.UserRepository
import com.example.service.AuthService
import com.example.service.JwtService
import io.mockk.every
import io.mockk.mockk
import mock.auth.createMockedJwtService
import org.jetbrains.exposed.sql.Database
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import java.util.*

abstract class AuthServiceTest {
    private lateinit var database: Database

    protected lateinit var userRepository: UserRepository
    protected lateinit var refreshTokenRepository: RefreshTokenRepository
    protected lateinit var jwtService: JwtService
    protected lateinit var authService: AuthService

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

        jwtService = createMockedJwtService()
        authService = AuthService(userRepository, refreshTokenRepository, jwtService)
    }


    @After
    fun tearDown() {
        TestDatabase.tearDown()
    }

    protected suspend fun registerAndGetUserData(
        registerData: RegisterData = RegisterData(
            name = "Test User",
            email = "test@example.com",
            password = "Password123!!"
        )
    ): Pair<User, TokenStrings> {
        val userTokens = authService.register(registerData)
        val user = userRepository.findByEmail(registerData.email)!!

        return user to userTokens
    }

    protected fun mockJwtRefreshOperations(userId: UUID, refreshTokenString: String) {
        mockk<DecodedJWT>().let { mockDecodedJwt ->
            every { mockDecodedJwt.getClaim("userId") } returns mockk {
                every { asString() } returns userId.toString()
            }

            every { jwtService.verifyToken(refreshTokenString, any()) } returns mockDecodedJwt
            every { jwtService.extractUserId(mockDecodedJwt, any()) } returns userId
        }
    }
}