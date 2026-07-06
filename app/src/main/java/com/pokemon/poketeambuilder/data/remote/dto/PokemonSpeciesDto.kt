package com.pokemon.poketeambuilder.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonSpeciesDto(
    @SerialName("is_legendary") val isLegendary: Boolean,
    @SerialName("is_mythical") val isMythical: Boolean,
    val generation: NamedApiResourceDto,
    @SerialName("evolution_chain") val evolutionChain: EvolutionChainRefDto
)

@Serializable
data class EvolutionChainRefDto(val url: String)

@Serializable
data class EvolutionChainDto(val chain: EvolutionNodeDto)

@Serializable
data class EvolutionNodeDto(
    val species: NamedApiResourceDto,
    @SerialName("evolves_to") val evolvesTo: List<EvolutionNodeDto>
)
