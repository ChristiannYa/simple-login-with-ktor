package com.example.config

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
)