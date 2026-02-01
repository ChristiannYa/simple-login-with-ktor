package com.example.routes.task

import com.example.config.taskRepository
import com.example.domain.TaskPriority
import com.example.dto.DtoRes
import com.example.dto.toDto
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getTasksByPriority() {
    get("/by-priority/{priority?}") {
        val priorityAsText = call.parameters["priority"]
        println("priority as text: $priorityAsText")

        if (priorityAsText == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        try {
            val taskPriorityEnum = TaskPriority.valueOf(priorityAsText.uppercase())
            println("priority as enum: $taskPriorityEnum")

            val tasksByPriority = call.taskRepository.tasksByPriority(taskPriorityEnum)

            if (tasksByPriority.isEmpty()) {
                call.respond(
                    HttpStatusCode.NotFound,
                    DtoRes.error("tasks by $priorityAsText priority is empty")
                )
                return@get
            }

            call.respond(
                HttpStatusCode.OK,
                DtoRes.success(
                    "tasks by $priorityAsText priority retrieved successfully",
                    mapOf("tasks" to tasksByPriority.map { it.toDto() })
                )
            )
        } catch (_: IllegalArgumentException) {
            call.respond(
                HttpStatusCode.BadRequest,
                DtoRes.error("error retrieving tasks by $priorityAsText priority [IAE]")
            )
        }
    }
}