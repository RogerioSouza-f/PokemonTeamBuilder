package com.pokemon.poketeambuilder.ui.screens.team

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.pokemon.poketeambuilder.data.model.Team
import com.pokemon.poketeambuilder.ui.components.PokemonSearchBar
import com.pokemon.poketeambuilder.viewmodel.PokemonSearchViewModel
import com.pokemon.poketeambuilder.viewmodel.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTeamScreen(
    navController: NavHostController,
    teamViewModel: TeamViewModel = hiltViewModel(),
    searchViewModel: PokemonSearchViewModel = hiltViewModel()
) {

    val formState by teamViewModel.formState.collectAsState()
    val searchState by searchViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        teamViewModel.startNewTeamForm()
    }

    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Criar Time")
                        Text(
                            "Monte sua equipe perfeita",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },

        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Button(
                    onClick = teamViewModel::saveTeam,
                    enabled = !formState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Text(
                        if (formState.isSaving)
                            "Salvando..."
                        else
                            "Salvar Time",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    OutlinedTextField(
                        value = formState.name,
                        onValueChange = teamViewModel::onNameChange,
                        label = { Text("Nome do time") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = teamViewModel::onDescriptionChange,
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "Sua equipe",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "${formState.selectedPokemon.size}/${Team.MAX_TEAM_SIZE}",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = {
                    formState.selectedPokemon.size.toFloat() /
                            Team.MAX_TEAM_SIZE.toFloat()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(formState.selectedPokemon) { pokemon ->

                    ElevatedCard(
                        modifier = Modifier.size(
                            width = 110.dp,
                            height = 130.dp
                        ),
                        elevation = elevatedCardElevation()
                    ) {

                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            IconButton(
                                onClick = {
                                    teamViewModel.removePokemonFromTeam(pokemon)
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remover"
                                )
                            }

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                AsyncImage(
                                    model = pokemon.imageUrl,
                                    contentDescription = pokemon.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(64.dp)
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    pokemon.name.replaceFirstChar {
                                        it.uppercase()
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                items(
                    Team.MAX_TEAM_SIZE -
                            formState.selectedPokemon.size
                ) {

                    ElevatedCard(
                        modifier = Modifier.size(
                            width = 110.dp,
                            height = 130.dp
                        )
                    ) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Adicionar",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Buscar Pokémon",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            PokemonSearchBar(
                query = searchState.query,
                onQueryChange = searchViewModel::onQueryChange,
                onSearch = searchViewModel::search
            )

            searchState.pokemon?.let { found ->

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            model = found.imageUrl,
                            contentDescription = found.name,
                            modifier = Modifier.size(72.dp)
                        )

                        Spacer(Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                found.name.replaceFirstChar {
                                    it.uppercase()
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Toque para adicionar ao time",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        FilledTonalButton(
                            onClick = {
                                teamViewModel.addPokemonToTeam(found)
                            },
                            enabled = formState.selectedPokemon.size <
                                    Team.MAX_TEAM_SIZE
                        ) {
                            Text("Adicionar")
                        }
                    }
                }
            }

            if (formState.error != null) {

                Spacer(Modifier.height(16.dp))

                Text(
                    text = formState.error!!.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}