package com.pokemon.poketeambuilder.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pokemon.poketeambuilder.ui.components.ErrorMessage
import com.pokemon.poketeambuilder.ui.components.PokemonSearchBar
import com.pokemon.poketeambuilder.ui.components.StatBar
import com.pokemon.poketeambuilder.ui.components.TypeChip
import com.pokemon.poketeambuilder.viewmodel.PokemonSearchViewModel

/**
 * Tela de Busca de Pokémon por nome, exibindo imagem, tipos,
 * habilidades, stats, peso e altura.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPokemonScreen(
    navController: NavHostController,
    viewModel: PokemonSearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Pokémon") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            PokemonSearchBar(
                query = state.query,
                onQueryChange = viewModel::onQueryChange,
                onSearch = viewModel::search
            )

            if (state.searchHistory.isNotEmpty() && state.pokemon == null) {
                Spacer(Modifier.height(12.dp))
                Text("Buscas recentes:", style = MaterialTheme.typography.labelLarge)
                LazyRow {
                    items(state.searchHistory) { term ->
                        androidx.compose.material3.AssistChip(
                            onClick = {
                                viewModel.onQueryChange(term)
                                viewModel.search()
                            },
                            label = { Text(term) },
                            modifier = Modifier.padding(end = 8.dp, top = 4.dp)
                        )
                    }
                }
            }

            when {
                state.isLoading -> Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }

                state.error != null -> ErrorMessage(error = state.error!!, onRetry = viewModel::search)

                state.pokemon != null -> {
                    val pokemon = state.pokemon!!
                    Column(
                        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            AsyncImage(
                                model = pokemon.imageUrl,
                                contentDescription = pokemon.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(160.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(pokemon.pokedexNumberFormatted + " " + pokemon.name, style = MaterialTheme.typography.headlineMedium)
                            IconButton(onClick = viewModel::toggleFavorite) {
                                Icon(
                                    if (pokemon.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favoritar"
                                )
                            }
                        }
                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            pokemon.types.forEach { TypeChip(it, modifier = Modifier.padding(end = 8.dp)) }
                        }
                        Text("Peso: ${pokemon.weightKg} kg   |   Altura: ${pokemon.heightM} m")
                        Text(
                            "Habilidades: " + pokemon.abilities.joinToString(", ") { it.name },
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("Estatísticas base", style = MaterialTheme.typography.titleMedium)
                        pokemon.stats.forEach { StatBar(it, modifier = Modifier.fillMaxWidth()) }
                    }
                }

                else -> Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { Text("Digite o nome de um Pokémon para buscar.") }
            }
        }
    }
}
