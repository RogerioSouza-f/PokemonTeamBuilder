package com.pokemon.poketeambuilder.data.repository.mapper

import com.pokemon.poketeambuilder.data.model.Pokemon
import com.pokemon.poketeambuilder.data.model.PokemonAbility
import com.pokemon.poketeambuilder.data.model.PokemonStat
import com.pokemon.poketeambuilder.data.model.PokemonSummary
import com.pokemon.poketeambuilder.data.model.PokemonType
import com.pokemon.poketeambuilder.data.remote.dto.EvolutionNodeDto
import com.pokemon.poketeambuilder.data.remote.dto.NamedApiResourceDto
import com.pokemon.poketeambuilder.data.remote.dto.PokemonDto
import com.pokemon.poketeambuilder.data.remote.dto.PokemonSpeciesDto


object PokemonMapper {


    fun extractIdFromUrl(url: String): Int =
        url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0

    fun toSummary(resource: NamedApiResourceDto): PokemonSummary {
        val id = extractIdFromUrl(resource.url)
        return PokemonSummary(
            id = id,
            name = resource.name.replaceFirstChar { it.uppercase() },
            imageUrl = officialArtworkUrl(id)
        )
    }

    fun officialArtworkUrl(id: Int): String =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

    fun toDomain(
        dto: PokemonDto,
        species: PokemonSpeciesDto? = null,
        evolutionChain: List<PokemonSummary> = emptyList(),
        isFavorite: Boolean = false
    ): Pokemon {
        val imageUrl = dto.sprites.other?.officialArtwork?.frontDefault
            ?: dto.sprites.frontDefault
            ?: officialArtworkUrl(dto.id)

        return Pokemon(
            id = dto.id,
            name = dto.name.replaceFirstChar { it.uppercase() },
            imageUrl = imageUrl,
            types = dto.types.sortedBy { it.slot }.map { PokemonType.fromApiValue(it.type.name) },
            abilities = dto.abilities.map { PokemonAbility(it.ability.name, it.isHidden) },
            stats = dto.stats.map {
                PokemonStat(PokemonStat.friendlyName(it.stat.name), it.baseStat)
            },
            weightKg = dto.weight / 10.0,   // hectogramas -> kg
            heightM = dto.height / 10.0,    // decímetros -> metros
            moves = dto.moves.map { it.move.name },
            isLegendary = species?.let { it.isLegendary || it.isMythical } ?: false,
            generation = species?.generation?.name,
            evolutionChain = evolutionChain,
            isFavorite = isFavorite
        )
    }


    fun flattenEvolutionChain(node: EvolutionNodeDto): List<PokemonSummary> {
        val result = mutableListOf<PokemonSummary>()
        fun visit(n: EvolutionNodeDto) {
            result.add(toSummary(n.species))
            n.evolvesTo.forEach { visit(it) }
        }
        visit(node)
        return result
    }
}
