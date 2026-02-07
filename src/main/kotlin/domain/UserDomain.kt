package com.example.domain

import java.time.Instant
import java.util.*

enum class UserType {
    USER, CONTRIBUTOR, ADMIN
}

data class User(
    val id: UUID,
    val name: String,
    val email: String,
    val passwordHash: String,
    val type: UserType,
    val isPremium: Boolean,
    val createdAt: Instant
)

data class UserCreate(
    val name: String,
    val email: String,
    val passwordHash: String
)

data class UserPrincipal(
    val id: UUID,
    val type: UserType,
    val isPremium: Boolean
)