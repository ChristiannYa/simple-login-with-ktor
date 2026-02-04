package com.example

import com.example.plugins.*
import com.example.routes.app.configureAppRoutes
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabases()
    configureSerialization()
    configureStatusPages()
    configureJwt()
    configureRoutes()
    configureAppRoutes()
}
