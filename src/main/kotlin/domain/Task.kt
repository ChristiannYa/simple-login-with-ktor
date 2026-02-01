package com.example.domain

import java.time.Instant
import java.util.UUID

// Core business logic and entities, independent of infrastructure

// Domain models are internal, they shouldn't know about serialization.
// That is an API concern. Only DTO's should be serializable

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    VITAL
}

data class Task(
    val id: Int,
    val userId: UUID,
    val name: String,
    val description: String,
    val priority: TaskPriority,
    val createdAt: Instant
) {
    fun isUrgent(): Boolean = priority == TaskPriority.VITAL
}

data class TaskAdd(
    val userId: UUID,
    val name: String,
    val description: String,
    val priority: TaskPriority
)