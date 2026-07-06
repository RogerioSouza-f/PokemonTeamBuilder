package com.pokemon.poketeambuilder.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pokemon.poketeambuilder.data.model.PokemonStat

/* Barra de progresso representando uma stat de batalha (ex: Ataque: 84/255). */
@Composable
fun StatBar(stat: PokemonStat, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(stat.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text(stat.baseValue.toString(), style = MaterialTheme.typography.bodyMedium)
        }
        LinearProgressIndicator(
            progress = { stat.baseValue / PokemonStat.MAX_STAT_VALUE.toFloat() },
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
        )
    }
}
