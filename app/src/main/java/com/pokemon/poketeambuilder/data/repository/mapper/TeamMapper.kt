package com.pokemon.poketeambuilder.data.repository.mapper

import com.pokemon.poketeambuilder.data.model.Team
import com.pokemon.poketeambuilder.data.remote.dto.TeamDto
import com.pokemon.poketeambuilder.data.remote.dto.TeamRequestDto

object TeamMapper {
    fun toDomain(dto: TeamDto): Team = Team(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        pokemonIds = dto.pokemon
    )

    fun toRequestDto(team: Team): TeamRequestDto = TeamRequestDto(
        name = team.name,
        description = team.description,
        pokemon = team.pokemonIds
    )
}
