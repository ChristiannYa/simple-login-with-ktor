package com.example.routes.task

import com.example.config.taskRepository
import com.example.dto.DtoRes
import com.example.dto.toDto
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getAllTasks() {
    get {
        val tasks = call.taskRepository.allTasks()

        call.respond(
            DtoRes.success(
                "tasks retrieved successfully",
                mapOf("tasks" to tasks.map { it.toDto() })
            )
        )
    }
}