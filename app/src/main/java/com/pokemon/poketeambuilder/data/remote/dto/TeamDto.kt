package com.pokemon.poketeambuilder.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Int? = null,
    val name: String,
    val description: String,
    val pokemon: List<Int>
)

@Serializable
data class TeamRequestDto(
    val name: String,
    val description: String,
    val pokemon: List<Int>
)
