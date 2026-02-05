package mock.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.example.config.JwtConfig

fun createMockJwtVerifier(jwtConfig: JwtConfig): JWTVerifier {
    return JWT
        .require(Algorithm.HMAC256(jwtConfig.secret))
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.issuer)
        .build()
}