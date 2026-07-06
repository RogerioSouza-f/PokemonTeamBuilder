package com.pokemon.poketeambuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemon.poketeambuilder.data.local.datastore.UserPreferences
import com.pokemon.poketeambuilder.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setDarkTheme(enabled) }
    }

    fun setTrainerName(name: String) {
        viewModelScope.launch { userPreferencesRepository.setTrainerName(name) }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { userPreferencesRepository.setLanguage(language) }
    }
}
