package com.example.plugins.scoped

import com.example.config.userPrincipal
import com.example.dto.DtoRes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

val RequiresPremiumPlugin = createRouteScopedPlugin(name = "RequiresPremiumLogin") {
    on(AuthenticationChecked) { call ->
        // `RequiresPremiumPlugin` is (should be) used within `AuthPlugin`, which guarantees
        // `userPrincipal`
        val userPrincipal = call.userPrincipal

        if (!userPrincipal.isPremium) {
            call.respond(
                HttpStatusCode.Forbidden,
                DtoRes.error("premium subscription required")
            )
        }
    }
}