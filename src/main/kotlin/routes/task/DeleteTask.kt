package com.example.routes.task

import com.example.config.taskRepository
import com.example.dto.DtoRes
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.deleteTask() {
    delete("/{task-name}") {
        val name = call.parameters["task-name"]
        if (name == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                DtoRes.error("task name cannot be empty")
            )
            return@delete
        }

        if (call.taskRepository.removeTask(name)) {
            call.respond(
                HttpStatusCode.OK,
                DtoRes.success(
                    "task deleted successfully",
                    mapOf("task_deleted" to name)
                )
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                DtoRes.error("task name not found")
            )
        }
    }
}