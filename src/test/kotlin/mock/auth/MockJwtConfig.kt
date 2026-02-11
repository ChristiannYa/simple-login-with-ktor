package mock.auth

import com.example.config.JwtPayload

val mockJwtPayload = JwtPayload(
    secret = "mock-secret-key-for-testing",
    issuer = "mock-issuer",
    audience = "mock-audience",
    realm = "mock-realm"
)