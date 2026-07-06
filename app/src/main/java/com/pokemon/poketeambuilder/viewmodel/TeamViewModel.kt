package com.pokemon.poketeambuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemon.poketeambuilder.data.model.Pokemon
import com.pokemon.poketeambuilder.data.model.Team
import com.pokemon.poketeambuilder.data.repository.PokemonRepository
import com.pokemon.poketeambuilder.data.repository.TeamRepository
import com.pokemon.poketeambuilder.network.AppError
import com.pokemon.poketeambuilder.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeamListUiState(
    val teams: List<Team> = emptyList(),
    val isLoading: Boolean = false,
    val error: AppError? = null
)

data class TeamFormUiState(
    val id: Int? = null,
    val name: String = "",
    val description: String = "",
    val selectedPokemon: List<Pokemon> = emptyList(),
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: AppError? = null
)

data class TeamDetailsUiState(
    val team: Team? = null,
    val pokemonDetails: List<Pokemon> = emptyList(),
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val deleted: Boolean = false
)

/**
 * ViewModel único responsável pelos fluxos de Time: listar, criar,
 * editar, ver detalhes e excluir. Centralizamos em um só ViewModel
 * porque todos compartilham o mesmo TeamRepository e o mesmo modelo
 * de formulário — evita duplicação entre CreateTeamScreen e EditTeamScreen.
 */
@HiltViewModel
class TeamViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(TeamListUiState())
    val listState: StateFlow<TeamListUiState> = _listState.asStateFlow()

    private val _formState = MutableStateFlow(TeamFormUiState())
    val formState: StateFlow<TeamFormUiState> = _formState.asStateFlow()

    private val _detailsState = MutableStateFlow(TeamDetailsUiState())
    val detailsState: StateFlow<TeamDetailsUiState> = _detailsState.asStateFlow()

    // ---------- LISTAGEM (GET /teams) ----------
    fun loadTeams() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            when (val result = teamRepository.getAllTeams()) {
                is NetworkResult.Success -> _listState.value =
                    TeamListUiState(teams = result.data, isLoading = false)
                is NetworkResult.Error -> _listState.value =
                    _listState.value.copy(isLoading = false, error = result.error)
                is NetworkResult.Loading -> Unit
            }
        }
    }

    // ---------- DETALHES (GET /teams/{id}) ----------
    fun loadTeamDetails(id: Int) {
        viewModelScope.launch {
            _detailsState.value = _detailsState.value.copy(isLoading = true, error = null)
            when (val teamResult = teamRepository.getTeamById(id)) {
                is NetworkResult.Success -> {
                    val team = teamResult.data
                    val pokemonList = team.pokemonIds.mapNotNull { pid ->
                        (pokemonRepository.getPokemonDetails(pid.toString()) as? NetworkResult.Success)?.data
                    }
                    _detailsState.value = TeamDetailsUiState(
                        team = team, pokemonDetails = pokemonList, isLoading = false
                    )
                }
                is NetworkResult.Error -> _detailsState.value =
                    _detailsState.value.copy(isLoading = false, error = teamResult.error)
                is NetworkResult.Loading -> Unit
            }
        }
    }

    // ---------- FORMULÁRIO (Criar / Editar) ----------
    fun startNewTeamForm() {
        _formState.value = TeamFormUiState()
    }

    fun startEditTeamForm(team: Team, pokemonList: List<Pokemon>) {
        _formState.value = TeamFormUiState(
            id = team.id, name = team.name, description = team.description,
            selectedPokemon = pokemonList
        )
    }

    fun onNameChange(name: String) {
        _formState.value = _formState.value.copy(name = name)
    }

    fun onDescriptionChange(description: String) {
        _formState.value = _formState.value.copy(description = description)
    }

    fun addPokemonToTeam(pokemon: Pokemon) {
        val current = _formState.value.selectedPokemon
        if (current.size < Team.MAX_TEAM_SIZE && current.none { it.id == pokemon.id }) {
            _formState.value = _formState.value.copy(selectedPokemon = current + pokemon)
        }
    }

    fun removePokemonFromTeam(pokemon: Pokemon) {
        _formState.value = _formState.value.copy(
            selectedPokemon = _formState.value.selectedPokemon.filterNot { it.id == pokemon.id }
        )
    }

    /** POST (criação) ou PUT (edição), dependendo se `id` já existe. */
    fun saveTeam() {
        val form = _formState.value
        if (form.name.isBlank() || form.selectedPokemon.isEmpty()) {
            _formState.value = form.copy(error = AppError.UnknownError("Preencha o nome e adicione ao menos 1 Pokémon."))
            return
        }

        val team = Team(
            id = form.id,
            name = form.name,
            description = form.description,
            pokemonIds = form.selectedPokemon.map { it.id }
        )

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSaving = true, error = null)
            val result = if (form.id == null) {
                teamRepository.createTeam(team)          // POST /teams
            } else {
                teamRepository.updateTeam(form.id, team)  // PUT /teams/{id}
            }
            when (result) {
                is NetworkResult.Success -> _formState.value =
                    _formState.value.copy(isSaving = false, isSuccess = true)
                is NetworkResult.Error -> _formState.value =
                    _formState.value.copy(isSaving = false, error = result.error)
                is NetworkResult.Loading -> Unit
            }
        }
    }

    // ---------- EXCLUSÃO (DELETE /teams/{id}) ----------
    fun deleteTeam(id: Int) {
        viewModelScope.launch {
            _detailsState.value = _detailsState.value.copy(isLoading = true)
            when (val result = teamRepository.deleteTeam(id)) {
                is NetworkResult.Success -> _detailsState.value =
                    _detailsState.value.copy(isLoading = false, deleted = true)
                is NetworkResult.Error -> _detailsState.value =
                    _detailsState.value.copy(isLoading = false, error = result.error)
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
