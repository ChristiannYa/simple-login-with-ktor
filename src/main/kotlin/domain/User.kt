package com.example.domain

import java.time.Instant
import java.util.*

data class User(
    val id: UUID,
    val name: String,
    val email: String,
    val passwordHash: String,
    val type: UserType,
    val isPremium: Boolean,
    val createdAt: Instant
)

enum class UserType {
    USER, CONTRIBUTOR, ADMIN
}

data class UserPrincipal(
    val id: UUID,
    val type: UserType,
    val isPremium: Boolean
)