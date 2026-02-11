package mock.auth

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.service.JwtService
import io.mockk.every
import io.mockk.mockk
import java.util.*

/**
 * Creates a mocked JwtService with defaults values for testing.
 * Default behavior can be overridden in individual tests
 */
fun createMockedJwtService(): JwtService {
    return mockk<JwtService>(relaxed = true).apply {
        // Default token generation
        every { generateAccessToken(any()) } returns "fake-access-token"
        every { generateRefreshToken(any()) } returns "fake-refresh-token"

        // Default token verification - returns a mock DecodedJWT
        every { verifyToken(any(), any()) } answers {
            mockk<DecodedJWT>()
        }

        // Default userId extraction - returns a random UUID
        // Can be overridden per test with specific UUIDs
        every { extractUserId(any(), any()) } returns UUID.randomUUID()
    }
}