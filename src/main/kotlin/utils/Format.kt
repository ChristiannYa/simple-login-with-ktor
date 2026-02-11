package com.example.utils

fun <T : Enum<T>> Enum<T>.prettify(): String =
    this.name
        .lowercase()
        .replace("_", " ")
        .replaceFirstChar { it.uppercase() }