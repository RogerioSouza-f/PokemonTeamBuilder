package com.pokemon.poketeambuilder.data.remote.api

import com.pokemon.poketeambuilder.data.remote.dto.TeamDto
import com.pokemon.poketeambuilder.data.remote.dto.TeamRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface TeamApiService {

    @GET("teams")
    suspend fun getAllTeams(): List<TeamDto>

    @GET("teams/{id}")
    suspend fun getTeamById(@Path("id") id: Int): TeamDto

    @POST("teams")
    suspend fun createTeam(@Body team: TeamRequestDto): Response<TeamDto>

    @PUT("teams/{id}")
    suspend fun updateTeam(@Path("id") id: Int, @Body team: TeamRequestDto): Response<TeamDto>

    @DELETE("teams/{id}")
    suspend fun deleteTeam(@Path("id") id: Int): Response<Unit>
}
