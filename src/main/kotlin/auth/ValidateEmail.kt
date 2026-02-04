package com.example.auth

val EMAIL_ADDRESS_PATTERN = Regex(
    "[a-zA-Z0-9+_.-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9-]{0,25})+"
)

fun String.isValidEmail(): Boolean = EMAIL_ADDRESS_PATTERN.matches(this)
