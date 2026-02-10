package mock.auth

import com.example.config.JwtContent

val mockJwtContent = JwtContent(
    secret = "mock-secret-key-for-testing",
    issuer = "mock-issuer",
    audience = "mock-audience",
    realm = "mock-realm"
)