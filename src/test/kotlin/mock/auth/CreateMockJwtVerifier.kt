package mock.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.example.config.JwtPayload

fun createMockJwtVerifier(jwtPayload: JwtPayload): JWTVerifier {
    return JWT
        .require(Algorithm.HMAC256(jwtPayload.secret))
        .withAudience(jwtPayload.audience)
        .withIssuer(jwtPayload.issuer)
        .build()
}