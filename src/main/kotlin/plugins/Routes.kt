package com.example.plugins

import com.example.config.TaskRepositoryKey
import com.example.config.UserRepositoryKey
import com.example.repository.TaskRepository
import com.example.repository.UserRepository
import com.example.routes.auth.authRoutes
import com.example.routes.task.taskRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutes() {
    attributes.put(TaskRepositoryKey, TaskRepository())
    attributes.put(UserRepositoryKey, UserRepository())

    routing {
        authRoutes()
        taskRoutes()
    }
}
