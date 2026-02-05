package com.example.routes.task

import com.example.config.taskRepository
import com.example.config.userPrincipal
import com.example.dto.DtoRes
import com.example.dto.toDto
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.getAllTasks() {
    authenticate("auth-jwt") {
        get {
            val userPrincipal = call.userPrincipal
            val tasks = call.taskRepository.allTasks(userPrincipal.id)

            call.respond(
                DtoRes.success(
                    "tasks retrieved successfully",
                    mapOf("tasks" to tasks.map { it.toDto() })
                )
            )
        }
    }
}