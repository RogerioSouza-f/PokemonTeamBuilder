package com.pokemon.poketeambuilder.data.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<PokemonType>,
    val abilities: List<PokemonAbility>,
    val stats: List<PokemonStat>,
    val weightKg: Double,
    val heightM: Double,
    val moves: List<String>,
    val isLegendary: Boolean = false,
    val generation: String? = null,
    val evolutionChain: List<PokemonSummary> = emptyList(),
    val isFavorite: Boolean = false
) {
    val pokedexNumberFormatted: String
        get() = "#" + id.toString().padStart(3, '0')
}
