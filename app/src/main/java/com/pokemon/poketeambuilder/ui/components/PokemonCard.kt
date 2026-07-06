package com.pokemon.poketeambuilder.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pokemon.poketeambuilder.data.model.PokemonSummary

/* Card usado na Lista de Pokémon: imagem + nome + número da Pokédex. */
@Composable
fun PokemonCard(
    pokemon: PokemonSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(96.dp).aspectRatio(1f)
            )
            Text(pokemon.name, style = MaterialTheme.typography.titleMedium)
            Text(
                "#" + pokemon.id.toString().padStart(3, '0'),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
