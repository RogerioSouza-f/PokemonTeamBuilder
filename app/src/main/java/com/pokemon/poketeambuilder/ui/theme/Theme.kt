package com.pokemon.poketeambuilder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PokedexRed,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = PokedexBlue,
    tertiary = PokedexYellow,
    background = PokedexBackgroundLight,
    surface = PokedexSurfaceLight,
    error = PokedexRedDark
)

private val DarkColors = darkColorScheme(
    primary = PokedexRed,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = PokedexBlue,
    tertiary = PokedexYellow,
    background = PokedexBackgroundDark,
    surface = PokedexSurfaceDark,
    error = PokedexRedDark
)

@Composable
fun PokeTeamBuilderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PokeTypography,
        shapes = PokeShapes,
        content = content
    )
}
