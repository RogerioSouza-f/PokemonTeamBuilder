package com.pokemon.poketeambuilder.data.model

data class Team(
    val id: Int? = null,
    val name: String,
    val description: String,
    val pokemonIds: List<Int>
) {
    companion object {
        const val MAX_TEAM_SIZE = 6
    }

    val isFull: Boolean get() = pokemonIds.size >= MAX_TEAM_SIZE
    val pokemonCount: Int get() = pokemonIds.size
}
