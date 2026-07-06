package com.pokemon.poketeambuilder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pokemon.poketeambuilder.data.model.PokemonType
import com.pokemon.poketeambuilder.utils.toColor

/* Chip colorido representando um tipo de Pokémon (Fogo, Água, etc.) */
@Composable
fun TypeChip(type: PokemonType, modifier: Modifier = Modifier) {
    val color = type.toColor()
    Text(
        text = type.displayName,
        color = Color.White,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}
