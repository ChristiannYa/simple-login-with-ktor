package com.example.utils

import org.mindrot.jbcrypt.BCrypt

fun main() {
    val password = "Pas123!!"
    val hash = BCrypt.hashpw(password, BCrypt.gensalt())

    println("==================================")
    println("Password: $password")
    println("Hash: $hash")
    println("==================================")
}