package mock.auth

import com.example.config.JwtConfig

val mockJwtConfig = JwtConfig(
    secret = "mock-secret-key-for-testing",
    issuer = "mock-issuer",
    audience = "mock-audience",
    realm = "mock-realm"
)