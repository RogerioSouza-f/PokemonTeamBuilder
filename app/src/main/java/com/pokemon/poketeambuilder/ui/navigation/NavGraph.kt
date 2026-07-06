package com.pokemon.poketeambuilder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pokemon.poketeambuilder.ui.screens.details.PokemonDetailsScreen
import com.pokemon.poketeambuilder.ui.screens.home.HomeScreen
import com.pokemon.poketeambuilder.ui.screens.search.PokemonListScreen
import com.pokemon.poketeambuilder.ui.screens.search.SearchPokemonScreen
import com.pokemon.poketeambuilder.ui.screens.settings.SettingsScreen
import com.pokemon.poketeambuilder.ui.screens.team.CreateTeamScreen
import com.pokemon.poketeambuilder.ui.screens.team.EditTeamScreen
import com.pokemon.poketeambuilder.ui.screens.team.TeamDetailsScreen
import com.pokemon.poketeambuilder.ui.screens.team.TeamListScreen

/*
 Grafo de navegação central do app.
 Cada rota corresponde a uma tela completa
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.SearchPokemon.route) {
            SearchPokemonScreen(navController = navController)
        }

        composable(Screen.PokemonList.route) {
            PokemonListScreen(navController = navController)
        }

        composable(
            route = Screen.PokemonDetails.route,
            arguments = listOf(navArgument("pokemonId") { type = NavType.StringType })
        ) {
            PokemonDetailsScreen(navController = navController)
        }

        composable(Screen.CreateTeam.route) {
            CreateTeamScreen(navController = navController)
        }

        composable(Screen.TeamList.route) {
            TeamListScreen(navController = navController)
        }

        composable(
            route = Screen.TeamDetails.route,
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
            TeamDetailsScreen(navController = navController, teamId = teamId)
        }

        composable(
            route = Screen.EditTeam.route,
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
            EditTeamScreen(navController = navController, teamId = teamId)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
