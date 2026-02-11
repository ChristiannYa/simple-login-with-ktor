package mock.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.JwtPayload
import com.example.domain.UserPrincipal
import java.util.*

fun createMockTestToken(userPrincipal: UserPrincipal, mockJwtPayload: JwtPayload): String {
    return JWT.create()
        .withAudience(mockJwtPayload.audience)
        .withIssuer(mockJwtPayload.issuer)
        .withClaim("userId", userPrincipal.id.toString())
        .withClaim("type", userPrincipal.type.name)
        .withClaim("isPremium", userPrincipal.isPremium)
        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
        .sign(Algorithm.HMAC256(mockJwtPayload.secret))
}