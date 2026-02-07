package com.example.plugins

import com.example.routes.auth.authRoutes
import com.example.routes.task.taskRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutes() {
    routing {
        authRoutes()
        taskRoutes()
    }
}
