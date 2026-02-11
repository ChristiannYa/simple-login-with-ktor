package com.example.plugins

import com.example.plugins.scoped.AuthPlugin
import com.example.plugins.scoped.RequiresPremiumPlugin
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.withAuth(build: Route.() -> Unit) = authenticate("auth-jwt") {
    install(AuthPlugin)
    build()
}

fun Route.withPremium(build: Route.() -> Unit): Route = apply {
    // local val bar: Route
    // val bar = this

    install(RequiresPremiumPlugin)
    build()
}


//fun Int.barZoo(zoo: Int.() -> Unit): Int = apply {
//    // local val bar: Int
//    // val bar = this
//
//    zoo()
//}