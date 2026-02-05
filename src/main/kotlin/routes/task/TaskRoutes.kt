package com.example.routes.task

import com.example.plugins.withAuth
import io.ktor.server.routing.*

fun Route.taskRoutes() {
    route("/tasks") {
        withAuth {
            getAllTasks()
        }

        getTaskByName()
        getTasksByPriority()
        addTask()
        deleteTask()
    }
}