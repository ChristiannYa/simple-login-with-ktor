package tests.auth.service

import com.example.db.suspendTransaction
import com.example.repository.IRefreshTokenRepository
import com.example.repository.IUserRepository
import com.example.service.AuthService
import com.example.service.JwtService
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.jetbrains.exposed.sql.Transaction
import org.junit.After
import org.junit.Before

abstract class AuthServiceTest {
    protected lateinit var userRepository: IUserRepository
    protected lateinit var refreshTokenRepository: IRefreshTokenRepository
    protected lateinit var jwtService: JwtService
    protected lateinit var authService: AuthService

    @Before
    fun setup() {
        userRepository = mockk()
        refreshTokenRepository = mockk()
        jwtService = mockk()
        authService = AuthService(userRepository, refreshTokenRepository, jwtService)

        // Mock the top-level functions
        mockkStatic("com.example.auth.CryptographyKt")

        // Mock suspendTransaction - execute the lambda immediately without DB connection
        mockkStatic("com.example.db.DatabaseUtilsKt")
        coEvery {
            suspendTransaction<Any>(block = any())
        } coAnswers {
            val block = arg<suspend Transaction.() -> Any>(0)
            val mockTransaction = mockk<Transaction>(relaxed = true)
            block(mockTransaction)
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}