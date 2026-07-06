package com.pokemon.poketeambuilder.ui.screens.team

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pokemon.poketeambuilder.ui.components.ErrorMessage
import com.pokemon.poketeambuilder.ui.components.LoadingIndicator
import com.pokemon.poketeambuilder.ui.components.TeamCard
import com.pokemon.poketeambuilder.ui.navigation.Screen
import com.pokemon.poketeambuilder.viewmodel.TeamViewModel

/** Tela "Meus Times": busca todos os times via GET /teams e exibe em cards. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamListScreen(navController: NavHostController, viewModel: TeamViewModel = hiltViewModel()) {
    val state by viewModel.listState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadTeams() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Times") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingIndicator(modifier = Modifier.padding(padding))
            state.error != null -> ErrorMessage(error = state.error!!, onRetry = viewModel::loadTeams, modifier = Modifier.padding(padding))
            state.teams.isEmpty() -> androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) { Text("Nenhum time criado ainda.") }
            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(state.teams) { team ->
                    TeamCard(
                        team = team,
                        onClick = { navController.navigate(Screen.TeamDetails.createRoute(team.id ?: 0)) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}
