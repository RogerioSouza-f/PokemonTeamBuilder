package com.pokemon.poketeambuilder.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pokemon.poketeambuilder.viewmodel.SettingsViewModel

/**
 * Tela de Configurações: tema claro/escuro, nome do treinador.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, viewModel: SettingsViewModel = hiltViewModel()) {
    val prefs by viewModel.preferences.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tema escuro", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = prefs.isDarkTheme, onCheckedChange = viewModel::setDarkTheme)
            }

            OutlinedTextField(
                value = prefs.trainerName,
                onValueChange = viewModel::setTrainerName,
                label = { Text("Nome do treinador") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Text("Idioma", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
            Row {
                listOf("pt" to "Português", "en" to "English").forEach { (code, label) ->
                    androidx.compose.material3.FilterChip(
                        selected = prefs.language == code,
                        onClick = { viewModel.setLanguage(code) },
                        label = { Text(label) },
                        modifier = Modifier.padding(end = 8.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}
