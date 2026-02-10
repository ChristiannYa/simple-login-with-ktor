package com.example

import com.example.config.JwtServiceKey
import com.example.config.configureDependencies
import com.example.plugins.*
import com.example.routes.app.configureAppRoutes
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Configure dependencies
    configureDependencies()

    // Configure infrastructure
    configureDatabases()
    configureSerialization()
    configureStatusPages()
    configureJwt(attributes[JwtServiceKey])

    // Configure routes
    configureRoutesValidation()
    configureAppRoutes()
    configureRoutes()
}
