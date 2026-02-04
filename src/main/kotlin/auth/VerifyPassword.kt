package com.example.auth

import org.mindrot.jbcrypt.BCrypt

fun verifyPassword(
    plainPassword: String,
    hashedPassword: String
): Boolean = BCrypt.checkpw(plainPassword, hashedPassword)