package com.example.routes.task

import io.ktor.server.routing.*

fun Route.taskRoutes() {
    route("/tasks") {
        getAllTasks()
        getTaskByName()
        getTasksByPriority()
        addTask()
        deleteTask()
    }
}