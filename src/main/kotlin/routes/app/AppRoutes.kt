package com.example.routes.app

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Infrastructure routes (static files, health checks, home page)
fun Application.configureAppRoutes() {
    routing {
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        get("/") {
            call.respondText("Hello World!")
        }

        // Non-task related routes can be added here
        // get("/ping") {
        //    call.respondText("pong")
        // }
    }
}
