package com.example.routes.task

import com.example.config.taskRepository
import com.example.dto.DtoRes
import com.example.dto.toDto
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getTaskByName() {
    get("/by-name/{task-name?}") {
        val name = call.parameters["task-name"]
        if (name.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                DtoRes.error("task-name cannot be empty")
            )
            return@get
        }

        val task = call.taskRepository.taskByName(name)
        if (task == null) {
            call.respond(
                HttpStatusCode.NotFound,
                DtoRes.error("task name not found")
            )
            return@get
        }

        call.respond(
            DtoRes.success(
                "task retrieved successfully",
                mapOf("task" to task.toDto())
            )
        )
    }
}