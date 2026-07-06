package com.pokemon.poketeambuilder.data.repository

import com.pokemon.poketeambuilder.data.model.Team
import com.pokemon.poketeambuilder.data.remote.api.TeamApiService
import com.pokemon.poketeambuilder.data.remote.dto.TeamDto
import com.pokemon.poketeambuilder.network.NetworkResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Testes unitários do TeamRepository, cobrindo os 4 verbos HTTP
 * (GET, POST, PUT, DELETE) usando MockK para simular o TeamApiService,
 * sem depender de rede real.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TeamRepositoryTest {

    private lateinit var apiService: TeamApiService
    private lateinit var repository: TeamRepository

    @Before
    fun setUp() {
        apiService = mockk()
        repository = TeamRepository(apiService)
    }

    @Test
    fun `getAllTeams retorna sucesso com lista de times`() = runTest {
        val dtos = listOf(TeamDto(id = 1, name = "Time Competitivo", description = "Batalhas", pokemon = listOf(25, 6)))
        coEvery { apiService.getAllTeams() } returns dtos

        val result = repository.getAllTeams()

        assertTrue(result is NetworkResult.Success)
        assertEquals(1, (result as NetworkResult.Success).data.size)
        assertEquals("Time Competitivo", result.data[0].name)
    }

    @Test
    fun `createTeam retorna sucesso quando API responde 201`() = runTest {
        val newTeam = Team(name = "Novo Time", description = "Teste", pokemonIds = listOf(1, 4, 7))
        val responseDto = TeamDto(id = 10, name = "Novo Time", description = "Teste", pokemon = listOf(1, 4, 7))
        coEvery { apiService.createTeam(any()) } returns Response.success(201, responseDto)

        val result = repository.createTeam(newTeam)

        assertTrue(result is NetworkResult.Success)
        assertEquals(10, (result as NetworkResult.Success).data.id)
    }

    @Test
    fun `updateTeam retorna sucesso quando API responde 200`() = runTest {
        val updated = Team(id = 1, name = "Time Atualizado", description = "Novo", pokemonIds = listOf(1))
        val responseDto = TeamDto(id = 1, name = "Time Atualizado", description = "Novo", pokemon = listOf(1))
        coEvery { apiService.updateTeam(1, any()) } returns Response.success(responseDto)

        val result = repository.updateTeam(1, updated)

        assertTrue(result is NetworkResult.Success)
        assertEquals("Time Atualizado", (result as NetworkResult.Success).data.name)
    }

    @Test
    fun `deleteTeam retorna sucesso quando API responde 204`() = runTest {
        coEvery { apiService.deleteTeam(1) } returns Response.success(204, null)

        val result = repository.deleteTeam(1)

        assertTrue(result is NetworkResult.Success)
    }

    @Test
    fun `deleteTeam retorna erro quando time nao existe`() = runTest {
        val errorResponse = Response.error<Unit>(
            404,
            "{\"detail\":\"Time não encontrado\"}".toResponseBody("application/json".toMediaType())
        )
        coEvery { apiService.deleteTeam(999) } returns errorResponse

        val result = repository.deleteTeam(999)

        assertTrue(result is NetworkResult.Error)
    }
}
