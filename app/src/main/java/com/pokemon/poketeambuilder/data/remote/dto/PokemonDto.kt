package com.pokemon.poketeambuilder.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonTypeSlotDto>,
    val abilities: List<PokemonAbilitySlotDto>,
    val stats: List<PokemonStatSlotDto>,
    val moves: List<PokemonMoveSlotDto>,
    val sprites: PokemonSpritesDto,
    val species: NamedApiResourceDto
)

@Serializable
data class PokemonTypeSlotDto(val slot: Int, val type: NamedApiResourceDto)

@Serializable
data class PokemonAbilitySlotDto(
    val ability: NamedApiResourceDto,
    @SerialName("is_hidden") val isHidden: Boolean,
    val slot: Int
)

@Serializable
data class PokemonStatSlotDto(
    @SerialName("base_stat") val baseStat: Int,
    val effort: Int,
    val stat: NamedApiResourceDto
)

@Serializable
data class PokemonMoveSlotDto(val move: NamedApiResourceDto)

@Serializable
data class PokemonSpritesDto(
    val other: OtherSpritesDto? = null,
    @SerialName("front_default") val frontDefault: String? = null
)

@Serializable
data class OtherSpritesDto(
    @SerialName("official-artwork") val officialArtwork: OfficialArtworkDto? = null
)

@Serializable
data class OfficialArtworkDto(@SerialName("front_default") val frontDefault: String? = null)

@Serializable
data class NamedApiResourceDto(val name: String, val url: String)
