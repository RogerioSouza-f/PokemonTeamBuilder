package com.pokemon.poketeambuilder.utils

import com.pokemon.poketeambuilder.network.AppError

fun AppError.toUserMessage(): String = this.message

fun String.capitalizeWords(): String =
    split(" ", "-").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
