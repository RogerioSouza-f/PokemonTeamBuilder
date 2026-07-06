package com.pokemon.poketeambuilder.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pokemon.poketeambuilder.data.model.Team

/* Card usado na tela "Meus Times": nome, descrição e quantidade de Pokémon. */
@Composable
fun TeamCard(team: Team, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(team.name, style = MaterialTheme.typography.titleLarge)
            Text(team.description, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${team.pokemonCount}/${Team.MAX_TEAM_SIZE} Pokémon",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
