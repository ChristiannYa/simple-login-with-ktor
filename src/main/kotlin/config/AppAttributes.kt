package com.example.config

import com.example.domain.UserPrincipal
import com.example.repository.ITaskRepository
import com.example.repository.IUserRepository
import com.example.service.JwtService
import io.ktor.server.application.*
import io.ktor.util.*

// Attribute keys
val UserPrincipalKey = AttributeKey<UserPrincipal>("UserPrincipal")
val TaskRepositoryKey = AttributeKey<ITaskRepository>("TaskRepository")
val UserRepositoryKey = AttributeKey<IUserRepository>("UserRepository")
val JwtConfigKey = AttributeKey<JwtConfig>("JwtConfig")
val JwtServiceKey = AttributeKey<JwtService>("JwtService")

// Extension properties for easy access
val ApplicationCall.taskRepository: ITaskRepository
    get() = application.attributes[TaskRepositoryKey]

val ApplicationCall.userRepository: IUserRepository
    get() = application.attributes[UserRepositoryKey]

val ApplicationCall.jwtConfig: JwtConfig
    get() = application.attributes[JwtConfigKey]

val ApplicationCall.jwtService: JwtService
    get() = application.attributes[JwtServiceKey]

// Request-scoped (stored per request by AuthPlugin)
val ApplicationCall.userPrincipal: UserPrincipal
    get() = attributes[UserPrincipalKey]
