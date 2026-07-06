package com.pokemon.poketeambuilder.data.repository

import com.pokemon.poketeambuilder.data.local.datastore.UserPreferences
import com.pokemon.poketeambuilder.data.local.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/*
 DataStore: tema claro/escuro, nome do treinador, último time aberto e idioma.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) {
    val userPreferences: Flow<UserPreferences> = dataStore.userPreferencesFlow

    suspend fun setDarkTheme(enabled: Boolean) = dataStore.setDarkTheme(enabled)
    suspend fun setTrainerName(name: String) = dataStore.setTrainerName(name)
    suspend fun setLastOpenedTeam(teamId: Int) = dataStore.setLastOpenedTeam(teamId)
    suspend fun setLanguage(language: String) = dataStore.setLanguage(language)
}
