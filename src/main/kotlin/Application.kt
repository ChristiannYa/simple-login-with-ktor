package com.example

import com.example.config.AuthServiceKey
import com.example.config.RefreshTokenRepositoryKey
import com.example.config.TaskRepositoryKey
import com.example.config.UserRepositoryKey
import com.example.plugins.*
import com.example.repository.RefreshTokenRepository
import com.example.repository.TaskRepository
import com.example.repository.UserRepository
import com.example.routes.app.configureAppRoutes
import com.example.service.AuthService
import com.example.service.JwtService
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Instantiate Repositories
    val taskRepository = TaskRepository()
    val userRepository = UserRepository()
    val refreshTokenRepository = RefreshTokenRepository()

    // Store repositories in app attributes
    attributes.put(TaskRepositoryKey, taskRepository)
    attributes.put(UserRepositoryKey, userRepository)
    attributes.put(RefreshTokenRepositoryKey, refreshTokenRepository)

    // Instantiate services
    val jwtService = JwtService(this)
    val authService = AuthService(userRepository, refreshTokenRepository, jwtService)

    // Store services in the app's attributes
    attributes.put(AuthServiceKey, authService)

    configureDatabases()
    configureSerialization()
    configureStatusPages()
    configureJwt(jwtService)
    configureRoutes()
    configureAppRoutes()
    configureRoutesValidation()
}
