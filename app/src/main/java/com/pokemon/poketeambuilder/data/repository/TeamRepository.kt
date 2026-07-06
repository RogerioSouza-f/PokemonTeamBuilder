package com.pokemon.poketeambuilder.data.repository

import com.pokemon.poketeambuilder.data.model.Team
import com.pokemon.poketeambuilder.data.remote.api.TeamApiService
import com.pokemon.poketeambuilder.data.repository.mapper.TeamMapper
import com.pokemon.poketeambuilder.network.AppError
import com.pokemon.poketeambuilder.network.NetworkResult
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepository @Inject constructor(
    private val teamApiService: TeamApiService
) {

    /*GET /teams */
    suspend fun getAllTeams(): NetworkResult<List<Team>> = safeCall {
        teamApiService.getAllTeams().map { TeamMapper.toDomain(it) }
    }

    /*GET /teams/{id} */
    suspend fun getTeamById(id: Int): NetworkResult<Team> = safeCall {
        TeamMapper.toDomain(teamApiService.getTeamById(id))
    }

    /*POST /teams */
    suspend fun createTeam(team: Team): NetworkResult<Team> = safeCall {
        val response = teamApiService.createTeam(TeamMapper.toRequestDto(team))
        if (response.isSuccessful) {
            response.body()?.let { TeamMapper.toDomain(it) }
                ?: throw IllegalStateException("Resposta vazia do servidor")
        } else {
            throw retrofit2.HttpException(response)
        }
    }

    /*PUT /teams/{id} */
    suspend fun updateTeam(id: Int, team: Team): NetworkResult<Team> = safeCall {
        val response = teamApiService.updateTeam(id, TeamMapper.toRequestDto(team))
        if (response.isSuccessful) {
            response.body()?.let { TeamMapper.toDomain(it) }
                ?: throw IllegalStateException("Resposta vazia do servidor")
        } else {
            throw retrofit2.HttpException(response)
        }
    }

    /*DELETE /teams/{id} */
    suspend fun deleteTeam(id: Int): NetworkResult<Unit> = safeCall {
        val response = teamApiService.deleteTeam(id)
        if (!response.isSuccessful) throw retrofit2.HttpException(response)
    }

    private suspend fun <T> safeCall(block: suspend () -> T): NetworkResult<T> {
        return try {
            NetworkResult.Success(block())
        } catch (e: HttpException) {
            NetworkResult.Error(AppError.ApiError(e.code(), e.message()))
        } catch (e: IOException) {
            NetworkResult.Error(AppError.NoInternet)
        } catch (e: Exception) {
            NetworkResult.Error(AppError.UnknownError(e.message))
        }
    }
}
