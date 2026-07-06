package com.pokemon.poketeambuilder.data.model

data class PokemonStat(
    val name: String,
    val baseValue: Int
) {
    companion object {
        const val MAX_STAT_VALUE = 255

        fun friendlyName(apiName: String): String = when (apiName) {
            "hp" -> "HP"
            "attack" -> "Ataque"
            "defense" -> "Defesa"
            "special-attack" -> "Ataque Especial"
            "special-defense" -> "Defesa Especial"
            "speed" -> "Velocidade"
            else -> apiName.replaceFirstChar { it.uppercase() }
        }
    }
}
