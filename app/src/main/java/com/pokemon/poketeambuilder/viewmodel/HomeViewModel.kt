package com.pokemon.poketeambuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemon.poketeambuilder.data.repository.TeamRepository
import com.pokemon.poketeambuilder.data.repository.UserPreferencesRepository
import com.pokemon.poketeambuilder.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val teamCount: Int = 0,
    val trainerName: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { prefs ->
                _uiState.value = _uiState.value.copy(trainerName = prefs.trainerName)
            }
        }
        refreshTeams()
    }

    fun refreshTeams() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = teamRepository.getAllTeams()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        teamCount = result.data.size,
                        isLoading = false
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
