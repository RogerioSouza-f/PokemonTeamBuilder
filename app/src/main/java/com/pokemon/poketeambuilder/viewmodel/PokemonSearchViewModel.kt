package com.pokemon.poketeambuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemon.poketeambuilder.data.model.Pokemon
import com.pokemon.poketeambuilder.data.repository.PokemonRepository
import com.pokemon.poketeambuilder.network.AppError
import com.pokemon.poketeambuilder.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PokemonSearchUiState(
    val query: String = "",
    val pokemon: Pokemon? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val searchHistory: List<String> = emptyList()
)

@HiltViewModel
class PokemonSearchViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonSearchUiState())
    val uiState: StateFlow<PokemonSearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            pokemonRepository.getSearchHistory().collect { history ->
                _uiState.value = _uiState.value.copy(searchHistory = history.map { it.query })
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun search() {
        val term = _uiState.value.query.trim()
        if (term.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = pokemonRepository.getPokemonDetails(term)) {
                is NetworkResult.Success -> {
                    pokemonRepository.saveSearchTerm(term)
                    _uiState.value = _uiState.value.copy(pokemon = result.data, isLoading = false)
                }
                is NetworkResult.Error -> _uiState.value =
                    _uiState.value.copy(error = result.error, isLoading = false, pokemon = null)
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun toggleFavorite() {
        val pokemon = _uiState.value.pokemon ?: return
        viewModelScope.launch {
            pokemonRepository.toggleFavorite(pokemon)
            _uiState.value = _uiState.value.copy(pokemon = pokemon.copy(isFavorite = !pokemon.isFavorite))
        }
    }
}
