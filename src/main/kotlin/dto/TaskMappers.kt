package com.example.dto

import com.example.domain.Task

// Convert domain model to API response
// Used by route handlers when sending data to client
fun Task.toDto() = TaskDto(
    this.id.toString(),
    this.name,
    this.description,
    this.priority.toString(),
    this.createdAt.toString()
)

// No need for `TaskDto.toDomain()` because the client never sends a
// complete `TaskDto`. Client can only send as far as `TaskAddRequestDto`
