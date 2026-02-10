package mock.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.JwtContent
import com.example.domain.UserPrincipal
import java.util.*

fun createMockTestToken(userPrincipal: UserPrincipal, mockJwtContent: JwtContent): String {
    return JWT.create()
        .withAudience(mockJwtContent.audience)
        .withIssuer(mockJwtContent.issuer)
        .withClaim("userId", userPrincipal.id.toString())
        .withClaim("type", userPrincipal.type.name)
        .withClaim("isPremium", userPrincipal.isPremium)
        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
        .sign(Algorithm.HMAC256(mockJwtContent.secret))
}