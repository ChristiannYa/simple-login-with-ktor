package com.example.dto

import com.example.domain.User

fun User.toDto() = UserDto(
    this.id.toString(),
    this.name,
    this.email,
    this.type.toString(),
    this.isPremium.toString(),
    this.createdAt.toString()
)