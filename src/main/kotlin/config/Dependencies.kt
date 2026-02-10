package com.example.config

import com.example.repository.RefreshTokenRepository
import com.example.repository.TaskRepository
import com.example.repository.UserRepository
import com.example.service.AuthService
import com.example.service.JwtService
import io.ktor.server.application.*

fun Application.configureDependencies() {
    // Instantiate repositories
    val taskRepository = TaskRepository()
    val userRepository = UserRepository()
    val refreshTokenRepository = RefreshTokenRepository()

    // Instantiate services
    val jwtService = JwtService(this)
    val authService = AuthService(userRepository, refreshTokenRepository, jwtService)

    // Register in application attributes
    attributes.put(TaskRepositoryKey, taskRepository)
    attributes.put(UserRepositoryKey, userRepository)
    attributes.put(RefreshTokenRepositoryKey, refreshTokenRepository)
    attributes.put(JwtServiceKey, jwtService)
    attributes.put(AuthServiceKey, authService)
}