package com.example.routes.auth

import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.authRoutes() {
    route("/auth") {
        login()
    }
}