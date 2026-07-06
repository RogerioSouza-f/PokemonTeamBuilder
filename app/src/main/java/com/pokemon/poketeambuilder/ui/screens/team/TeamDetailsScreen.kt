package com.pokemon.poketeambuilder.ui.screens.team

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pokemon.poketeambuilder.ui.components.ErrorMessage
import com.pokemon.poketeambuilder.ui.components.LoadingIndicator
import com.pokemon.poketeambuilder.ui.components.TypeChip
import com.pokemon.poketeambuilder.ui.navigation.Screen
import com.pokemon.poketeambuilder.viewmodel.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsScreen(
    navController: NavHostController,
    teamId: Int,
    viewModel: TeamViewModel = hiltViewModel()
) {
    val state by viewModel.detailsState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(teamId) { viewModel.loadTeamDetails(teamId) }

    LaunchedEffect(state.deleted) {
        if (state.deleted) navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.team?.name ?: "Detalhes do Time") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.team?.let {
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, "Confira meu time '${it.name}' no PokéTeam Builder! Pokémon: ${it.pokemonIds.joinToString(", ")}")
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Compartilhar time"))
                        }
                    }) { Icon(Icons.Default.Share, contentDescription = "Compartilhar") }

                    IconButton(onClick = { navController.navigate(Screen.EditTeam.createRoute(teamId)) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingIndicator(modifier = Modifier.padding(padding))
            state.error != null -> ErrorMessage(error = state.error!!, onRetry = { viewModel.loadTeamDetails(teamId) }, modifier = Modifier.padding(padding))
            state.team != null -> {


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    if (!state.team!!.description.isNull_or_Blank()) { // Se houver propriedade tratada no modelo
                        item {
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Sobre o Time",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = state.team!!.description,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Integrantes (${state.pokemonDetails.size}/6)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    // Lista de Pokémon
                    items(state.pokemonDetails) { pokemon ->
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Imagem
                                AsyncImage(
                                    model = pokemon.imageUrl,
                                    contentDescription = pokemon.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(80.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pokemon.name.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Fileira de Tipos
                                    Row(modifier = Modifier.padding(vertical = 6.dp)) {
                                        pokemon.types.forEach { TypeChip(it, modifier = Modifier.padding(end = 4.dp)) }
                                    }

                                    // Stats principais
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        MiniStatDisplay("HP", pokemon.stats.getOrNull(0)?.baseValue)
                                        MiniStatDisplay("ATK", pokemon.stats.getOrNull(1)?.baseValue)
                                        MiniStatDisplay("DEF", pokemon.stats.getOrNull(2)?.baseValue)
                                        MiniStatDisplay("SPATK", pokemon.stats.getOrNull(5)?.baseValue)
                                        MiniStatDisplay("SPDEF", pokemon.stats.getOrNull(3)?.baseValue)
                                        MiniStatDisplay("SPEED", pokemon.stats.getOrNull(4)?.baseValue)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir time") },
            text = { Text("Tem certeza que deseja excluir este time? Essa ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteTeam(teamId)
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// Componente auxiliar para deixar os status organizados e limpos
@Composable
fun MiniStatDisplay(label: String, value: Int?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value?.toString() ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// Extensão utilitária caso queira checar Strings nulas/vazias sem quebrar o build
private fun String?.isNull_or_Blank(): Boolean = this == null || this.trim().isEmpty()