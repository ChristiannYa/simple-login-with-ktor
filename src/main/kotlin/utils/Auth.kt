package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.JwtConfig
import com.example.domain.UserPrincipal
import org.mindrot.jbcrypt.BCrypt
import java.util.*

val EMAIL_ADDRESS_PATTERN = Regex(
    "[a-zA-Z0-9+_.-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9-]{0,25})+"
)

fun createJwt(userPrincipal: UserPrincipal, config: JwtConfig): String {
    return JWT
        .create()
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .withClaim("userId", userPrincipal.id.toString())
        .withClaim("type", userPrincipal.type.name)
        .withClaim("isPremium", userPrincipal.isPremium)
        .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 60 * 24))
        .sign(Algorithm.HMAC256(config.secret))
}

fun String.isValidEmail(): Boolean = EMAIL_ADDRESS_PATTERN.matches(this)

fun verifyPassword(
    plainPassword: String,
    hashedPassword: String
): Boolean = BCrypt.checkpw(plainPassword, hashedPassword)