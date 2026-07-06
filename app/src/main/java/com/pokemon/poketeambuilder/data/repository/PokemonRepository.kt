package com.pokemon.poketeambuilder.data.repository

import com.pokemon.poketeambuilder.data.local.dao.FavoritePokemonDao
import com.pokemon.poketeambuilder.data.local.dao.RecentPokemonDao
import com.pokemon.poketeambuilder.data.local.dao.SearchHistoryDao
import com.pokemon.poketeambuilder.data.local.entity.FavoritePokemonEntity
import com.pokemon.poketeambuilder.data.local.entity.RecentPokemonEntity
import com.pokemon.poketeambuilder.data.local.entity.SearchHistoryEntity
import com.pokemon.poketeambuilder.data.model.Pokemon
import com.pokemon.poketeambuilder.data.model.PokemonSummary
import com.pokemon.poketeambuilder.data.remote.api.PokemonApiService
import com.pokemon.poketeambuilder.data.repository.mapper.PokemonMapper
import com.pokemon.poketeambuilder.network.AppError
import com.pokemon.poketeambuilder.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val pokemonApiService: PokemonApiService,
    private val favoriteDao: FavoritePokemonDao,
    private val recentDao: RecentPokemonDao,
    private val searchHistoryDao: SearchHistoryDao
) {

    /*Busca um Pokémon pelo nome ou número + evoluções. */
    suspend fun getPokemonDetails(nameOrId: String): NetworkResult<Pokemon> {
        return try {
            val normalized = nameOrId.trim().lowercase()
            val pokemonDto = pokemonApiService.getPokemonByNameOrId(normalized)

            val speciesDto = try {
                pokemonApiService.getPokemonSpecies(normalized)
            } catch (e: Exception) {
                null
            }

            val evolutionSummaries: List<PokemonSummary> = speciesDto?.let { species ->
                try {
                    val chainDto = pokemonApiService.getEvolutionChain(species.evolutionChain.url)
                    PokemonMapper.flattenEvolutionChain(chainDto.chain)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()

            val isFavorite = favoriteDao.isFavorite(pokemonDto.id).first()

            val pokemon = PokemonMapper.toDomain(
                dto = pokemonDto,
                species = speciesDto,
                evolutionChain = evolutionSummaries,
                isFavorite = isFavorite
            )

            // Registra como "visto recentemente"
            recentDao.addRecent(
                RecentPokemonEntity(pokemon.id, pokemon.name, pokemon.imageUrl)
            )

            NetworkResult.Success(pokemon)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                NetworkResult.Error(AppError.PokemonNotFound)
            } else {
                NetworkResult.Error(AppError.ApiError(e.code(), e.message()))
            }
        } catch (e: IOException) {
            NetworkResult.Error(AppError.NoInternet)
        } catch (e: Exception) {
            NetworkResult.Error(AppError.UnknownError(e.message))
        }
    }

    suspend fun getPokemonList(limit: Int, offset: Int): NetworkResult<List<PokemonSummary>> {
        return try {
            val response = pokemonApiService.getPokemonList(limit, offset)
            val summaries = response.results.map { PokemonMapper.toSummary(it) }
            NetworkResult.Success(summaries)
        } catch (e: HttpException) {
            NetworkResult.Error(AppError.ApiError(e.code(), e.message()))
        } catch (e: IOException) {
            NetworkResult.Error(AppError.NoInternet)
        } catch (e: Exception) {
            NetworkResult.Error(AppError.UnknownError(e.message))
        }
    }

    suspend fun saveSearchTerm(term: String) {
        if (term.isNotBlank()) searchHistoryDao.addSearch(SearchHistoryEntity(query = term))
    }

    fun getSearchHistory(): Flow<List<SearchHistoryEntity>> = searchHistoryDao.getSearchHistory()

    fun getRecentPokemon(): Flow<List<RecentPokemonEntity>> = recentDao.getRecentPokemon()

    fun getFavorites(): Flow<List<FavoritePokemonEntity>> = favoriteDao.getAllFavorites()

    fun isFavorite(pokemonId: Int): Flow<Boolean> = favoriteDao.isFavorite(pokemonId)

    suspend fun toggleFavorite(pokemon: Pokemon) {
        val isFav = favoriteDao.isFavorite(pokemon.id).first()
        if (isFav) {
            favoriteDao.removeFavoriteById(pokemon.id)
        } else {
            favoriteDao.addFavorite(
                FavoritePokemonEntity(pokemon.id, pokemon.name, pokemon.imageUrl)
            )
        }
    }
}
