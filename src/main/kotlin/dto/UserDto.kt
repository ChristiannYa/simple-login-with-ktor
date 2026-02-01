package com.example.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,

    @SerialName("user_type")
    val userType: String,

    @SerialName("is_premium")
    val isPremium: String,

    @SerialName("created_at")
    val createdAt: String
)

@Serializable
data class UserAddRequestDto(
    val name: String,
    val email: String,

    @SerialName("user_type")
    val userType: String
)