package com.example.plugins.scoped

import com.example.config.UserPrincipalKey
import com.example.domain.UserPrincipal
import com.example.dto.DtoRes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

val AuthPlugin = createRouteScopedPlugin(name = "AuthPlugin") {
    onCall { call ->
        val userPrincipal = call.principal<UserPrincipal>() ?: run {
            call.respond(
                HttpStatusCode.Unauthorized,
                DtoRes.error("authentication required")
            )
            return@onCall
        }

        call.attributes.put(UserPrincipalKey, userPrincipal)
    }
}