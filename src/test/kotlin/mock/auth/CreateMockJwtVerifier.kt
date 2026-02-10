package mock.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.example.config.JwtContent

fun createMockJwtVerifier(jwtContent: JwtContent): JWTVerifier {
    return JWT
        .require(Algorithm.HMAC256(jwtContent.secret))
        .withAudience(jwtContent.audience)
        .withIssuer(jwtContent.issuer)
        .build()
}