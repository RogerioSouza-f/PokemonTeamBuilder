package com.pokemon.poketeambuilder.data.model

enum class PokemonType(val displayName: String) {
    NORMAL("Normal"), FIRE("Fogo"), WATER("Água"), ELECTRIC("Elétrico"),
    GRASS("Planta"), ICE("Gelo"), FIGHTING("Lutador"), POISON("Venenoso"),
    GROUND("Terrestre"), FLYING("Voador"), PSYCHIC("Psíquico"), BUG("Inseto"),
    ROCK("Pedra"), GHOST("Fantasma"), DRAGON("Dragão"), DARK("Sombrio"),
    STEEL("Aço"), FAIRY("Fada"), UNKNOWN("Desconhecido");

    companion object {
        fun fromApiValue(value: String): PokemonType =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
    }
}
