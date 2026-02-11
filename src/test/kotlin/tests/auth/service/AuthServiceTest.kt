package tests.auth.service

import TestDatabase
import com.example.repository.RefreshTokenRepository
import com.example.repository.UserRepository
import com.example.service.AuthService
import com.example.service.JwtService
import mock.auth.createMockedJwtService
import org.jetbrains.exposed.sql.Database
import org.junit.After
import org.junit.AfterClass
import org.junit.Before

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
}