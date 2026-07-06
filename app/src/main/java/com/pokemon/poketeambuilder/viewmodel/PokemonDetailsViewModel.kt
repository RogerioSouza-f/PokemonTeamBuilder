package com.pokemon.poketeambuilder.viewmodel

import androidx.lifecycle.SavedStateHandle
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

data class PokemonDetailsUiState(
    val pokemon: Pokemon? = null,
    val isLoading: Boolean = true,
    val error: AppError? = null
)

@HiltViewModel
class PokemonDetailsViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pokemonId: String = checkNotNull(savedStateHandle["pokemonId"])

    private val _uiState = MutableStateFlow(PokemonDetailsUiState())
    val uiState: StateFlow<PokemonDetailsUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = pokemonRepository.getPokemonDetails(pokemonId)) {
                is NetworkResult.Success -> _uiState.value =
                    _uiState.value.copy(pokemon = result.data, isLoading = false)
                is NetworkResult.Error -> _uiState.value =
                    _uiState.value.copy(error = result.error, isLoading = false)
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
