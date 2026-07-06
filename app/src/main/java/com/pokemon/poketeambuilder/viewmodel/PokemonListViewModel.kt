package com.pokemon.poketeambuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemon.poketeambuilder.data.model.PokemonSummary
import com.pokemon.poketeambuilder.data.repository.PokemonRepository
import com.pokemon.poketeambuilder.network.AppError
import com.pokemon.poketeambuilder.network.NetworkResult
import com.pokemon.poketeambuilder.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder { ALPHABETICAL, NUMERICAL }

data class PokemonListUiState(
    val items: List<PokemonSummary> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: AppError? = null,
    val currentOffset: Int = 0,
    val endReached: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NUMERICAL
)

/**
 * ViewModel da tela de Listagem de Pokémon com paginação (scroll infinito).
 */
@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || state.endReached) return

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = state.items.isEmpty(),
                isLoadingMore = state.items.isNotEmpty()
            )
            when (
                val result = pokemonRepository.getPokemonList(
                    Constants.DEFAULT_PAGE_SIZE,
                    state.currentOffset
                )
            ) {
                is NetworkResult.Success -> {
                    val newItems = state.items + result.data
                    _uiState.value = _uiState.value.copy(
                        items = sortItems(newItems, state.sortOrder),
                        isLoading = false,
                        isLoadingMore = false,
                        currentOffset = state.currentOffset + Constants.DEFAULT_PAGE_SIZE,
                        endReached = result.data.isEmpty()
                    )
                }
                is NetworkResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false, isLoadingMore = false, error = result.error
                )
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun changeSortOrder(order: SortOrder) {
        _uiState.value = _uiState.value.copy(
            sortOrder = order,
            items = sortItems(_uiState.value.items, order)
        )
    }

    private fun sortItems(items: List<PokemonSummary>, order: SortOrder): List<PokemonSummary> =
        when (order) {
            SortOrder.ALPHABETICAL -> items.sortedBy { it.name }
            SortOrder.NUMERICAL -> items.sortedBy { it.id }
        }
}
