package com.example.auth

import org.mindrot.jbcrypt.BCrypt

fun hashPassword(plainPassword: String): String =
    BCrypt.hashpw(plainPassword, BCrypt.gensalt())


fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean =
    BCrypt.checkpw(plainPassword, hashedPassword)