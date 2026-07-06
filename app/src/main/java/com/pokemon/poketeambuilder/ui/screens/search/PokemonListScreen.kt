package com.pokemon.poketeambuilder.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pokemon.poketeambuilder.ui.components.ErrorMessage
import com.pokemon.poketeambuilder.ui.components.PokemonCard
import com.pokemon.poketeambuilder.ui.navigation.Screen
import com.pokemon.poketeambuilder.viewmodel.PokemonListViewModel
import com.pokemon.poketeambuilder.viewmodel.SortOrder

/**
 * Pokédex — listagem paginada com scroll
 * ordenação (número / alfabética).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    navController: NavHostController,
    viewModel: PokemonListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Pokédex",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                        )
                        Text(
                            text = when (state.sortOrder) {
                                SortOrder.NUMERICAL -> "Por número"
                                SortOrder.ALPHABETICAL -> "Alfabética"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    FilledTonalIconButton(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    Box {
                        FilledTonalIconButton(
                            onClick = { showSortMenu = true },
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Icon(
                                imageVector = if (state.sortOrder == SortOrder.ALPHABETICAL)
                                    Icons.Default.SortByAlpha else Icons.Default.Tag,
                                contentDescription = "Ordenar",
                            )
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            SortMenuItem(
                                label = "Por número",
                                icon = Icons.Default.Tag,
                                selected = state.sortOrder == SortOrder.NUMERICAL,
                                onClick = {
                                    viewModel.changeSortOrder(SortOrder.NUMERICAL)
                                    showSortMenu = false
                                },
                            )
                            SortMenuItem(
                                label = "Alfabética",
                                icon = Icons.Default.SortByAlpha,
                                selected = state.sortOrder == SortOrder.ALPHABETICAL,
                                onClick = {
                                    viewModel.changeSortOrder(SortOrder.ALPHABETICAL)
                                    showSortMenu = false
                                },
                            )
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundBrush),
        ) {
            when {
                state.isLoading && state.items.isEmpty() -> LoadingState()

                state.error != null && state.items.isEmpty() -> ErrorMessage(
                    error = state.error!!,
                    onRetry = viewModel::loadNextPage,
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.items, key = { it.id }) { pokemon ->
                        PokemonCard(
                            pokemon = pokemon,
                            onClick = {
                                navController.navigate(
                                    Screen.PokemonDetails.createRoute(pokemon.id.toString())
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp)),
                        )
                    }

                    if (!state.endReached) {
                        item {
                            LaunchedEffect(state.items.size) { viewModel.loadNextPage() }
                            LoadingFooter()
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = state.isLoading && state.items.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 4.dp,
                    shadowElevation = 6.dp,
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(14.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Carregando mais...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(strokeWidth = 3.dp)
        Spacer(Modifier.height(12.dp))
        Text(
            "Carregando Pokédex...",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun LoadingFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun SortMenuItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                label,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        },
        leadingIcon = { Icon(icon, contentDescription = null) },
        trailingIcon = {
            if (selected) Icon(Icons.Default.Check, contentDescription = null)
        },
        onClick = onClick,
    )
}
