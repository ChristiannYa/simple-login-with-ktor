package com.example.dto

import kotlinx.serialization.Serializable

// API contracts - how data crosses boundaries (HTTP, gRPC, etc.)

@Serializable
data class TaskDto(
    val id: String,
    // No userId exposed to API
    val name: String,
    val description: String,
    val priority: String,
    val createdAt: String
)

@Serializable
data class TaskAddRequestDto(
    val name: String,
    val description: String,
    val priority: String
)

