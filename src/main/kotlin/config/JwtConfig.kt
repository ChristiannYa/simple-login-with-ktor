package com.example.config

import java.time.Duration

object TokenConfiguration {
    val ACCESS_TOKEN_DURATION: Duration = Duration.ofHours(1)
    val REFRESH_TOKEN_DURATION: Duration = Duration.ofDays(1)
}

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
)