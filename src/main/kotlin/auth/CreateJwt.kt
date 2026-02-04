package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.config.JwtConfig
import com.example.domain.UserPrincipal
import java.util.*

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