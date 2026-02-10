package com.example.config

import java.time.Duration

object TokenDuration {
    val ACCESS_TOKEN: Duration = Duration.ofHours(1)
    val REFRESH_TOKEN: Duration = Duration.ofDays(1)
}

data class JwtContent(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
)