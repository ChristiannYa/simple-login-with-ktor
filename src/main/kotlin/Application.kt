package com.example

import com.example.plugins.configureDatabases
import com.example.plugins.configureJwt
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPages
import com.example.routes.app.configureAppRoutes
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Plugins
    configureDatabases()
    configureSerialization()
    configureStatusPages()
    configureJwt()

    // Routes
    configureAppRoutes()
    configureRoutes()
}
