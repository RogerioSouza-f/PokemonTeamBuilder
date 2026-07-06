package com.pokemon.poketeambuilder.utils

import androidx.compose.ui.graphics.Color
import com.pokemon.poketeambuilder.data.model.PokemonType
import com.pokemon.poketeambuilder.ui.theme.*

fun PokemonType.toColor(): Color = when (this) {
    PokemonType.NORMAL -> TypeNormal
    PokemonType.FIRE -> TypeFire
    PokemonType.WATER -> TypeWater
    PokemonType.ELECTRIC -> TypeElectric
    PokemonType.GRASS -> TypeGrass
    PokemonType.ICE -> TypeIce
    PokemonType.FIGHTING -> TypeFighting
    PokemonType.POISON -> TypePoison
    PokemonType.GROUND -> TypeGround
    PokemonType.FLYING -> TypeFlying
    PokemonType.PSYCHIC -> TypePsychic
    PokemonType.BUG -> TypeBug
    PokemonType.ROCK -> TypeRock
    PokemonType.GHOST -> TypeGhost
    PokemonType.DRAGON -> TypeDragon
    PokemonType.DARK -> TypeDark
    PokemonType.STEEL -> TypeSteel
    PokemonType.FAIRY -> TypeFairy
    PokemonType.UNKNOWN -> TypeUnknown
}
