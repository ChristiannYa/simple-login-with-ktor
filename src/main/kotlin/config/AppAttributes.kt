package com.example.config

import com.example.domain.UserPrincipal
import com.example.repository.ITaskRepository
import com.example.repository.IUserRepository
import io.ktor.server.application.*
import io.ktor.util.*

// Attribute keys
val UserPrincipalKey = AttributeKey<UserPrincipal>("UserPrincipal")
val TaskRepositoryKey = AttributeKey<ITaskRepository>("TaskRepository")
val UserRepositoryKey = AttributeKey<IUserRepository>("UserRepository")
val JwtConfigKey = AttributeKey<JwtConfig>("JwtConfig")

// Extension properties for easy access
val ApplicationCall.taskRepository: ITaskRepository
    get() = application.attributes[TaskRepositoryKey]

val ApplicationCall.userRepository: IUserRepository
    get() = application.attributes[UserRepositoryKey]

val ApplicationCall.jwtConfig: JwtConfig
    get() = application.attributes[JwtConfigKey]

// Request-scoped (stored per request by AuthPlugin)
val ApplicationCall.userPrincipal: UserPrincipal
    get() = attributes[UserPrincipalKey]
