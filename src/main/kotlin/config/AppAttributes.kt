package com.example.config

import com.example.domain.UserPrincipal
import com.example.repository.IRefreshTokenRepository
import com.example.repository.ITaskRepository
import com.example.repository.IUserRepository
import com.example.service.AuthService
import io.ktor.server.application.*
import io.ktor.util.*

// --------------
// ATTRIBUTE KEYS
// --------------
val UserPrincipalKey = AttributeKey<UserPrincipal>("UserPrincipal")

val TaskRepositoryKey = AttributeKey<ITaskRepository>("TaskRepository")
val UserRepositoryKey = AttributeKey<IUserRepository>("UserRepository")
val RefreshTokenRepositoryKey = AttributeKey<IRefreshTokenRepository>("RefreshTokenRepository")

val AuthServiceKey = AttributeKey<AuthService>("AuthService")

// --------------------
// EXTENSION PROPERTIES
// --------------------
val ApplicationCall.userPrincipal: UserPrincipal
    get() = attributes[UserPrincipalKey] // Request-scoped (stored per request by AuthPlugin)


val ApplicationCall.taskRepository: ITaskRepository
    get() = application.attributes[TaskRepositoryKey]

val ApplicationCall.userRepository: IUserRepository
    get() = application.attributes[UserRepositoryKey]

val ApplicationCall.refreshTokenRepository: IRefreshTokenRepository
    get() = application.attributes[RefreshTokenRepositoryKey]

val ApplicationCall.authService: AuthService
    get() = application.attributes[AuthServiceKey]
