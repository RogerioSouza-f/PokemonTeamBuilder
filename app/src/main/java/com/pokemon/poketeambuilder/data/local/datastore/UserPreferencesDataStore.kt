package com.pokemon.poketeambuilder.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class UserPreferences(
    val isDarkTheme: Boolean = false,
    val trainerName: String = "",
    val lastOpenedTeamId: Int? = null,
    val language: String = "pt"
)

class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val TRAINER_NAME = stringPreferencesKey("trainer_name")
        val LAST_TEAM_ID = intPreferencesKey("last_team_id")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            isDarkTheme = prefs[Keys.DARK_THEME] ?: false,
            trainerName = prefs[Keys.TRAINER_NAME] ?: "",
            lastOpenedTeamId = prefs[Keys.LAST_TEAM_ID],
            language = prefs[Keys.LANGUAGE] ?: "pt"
        )
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setTrainerName(name: String) {
        dataStore.edit { it[Keys.TRAINER_NAME] = name }
    }

    suspend fun setLastOpenedTeam(teamId: Int) {
        dataStore.edit { it[Keys.LAST_TEAM_ID] = teamId }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { it[Keys.LANGUAGE] = language }
    }
}
