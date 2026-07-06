package com.pokemon.poketeambuilder.data.remote.api

import com.pokemon.poketeambuilder.data.remote.dto.EvolutionChainDto
import com.pokemon.poketeambuilder.data.remote.dto.PokemonDto
import com.pokemon.poketeambuilder.data.remote.dto.PokemonListResponseDto
import com.pokemon.poketeambuilder.data.remote.dto.PokemonSpeciesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface PokemonApiService {

    @GET("pokemon/{nameOrId}")
    suspend fun getPokemonByNameOrId(@Path("nameOrId") nameOrId: String): PokemonDto

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponseDto

    @GET("pokemon-species/{nameOrId}")
    suspend fun getPokemonSpecies(@Path("nameOrId") nameOrId: String): PokemonSpeciesDto

    @GET
    suspend fun getEvolutionChain(@Url url: String): EvolutionChainDto
}
