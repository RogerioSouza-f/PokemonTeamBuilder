package com.pokemon.poketeambuilder.ui.navigation

/*
  Home -> Buscar/Listar Pokémon -> Detalhes Pokémon -> Criar Time ->
  Lista de Times -> Detalhes do Time -> Editar Time -> Configurações
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object SearchPokemon : Screen("search_pokemon")
    data object PokemonList : Screen("pokemon_list")
    data object PokemonDetails : Screen("pokemon_details/{pokemonId}") {
        fun createRoute(pokemonId: String) = "pokemon_details/$pokemonId"
    }
    data object CreateTeam : Screen("create_team")
    data object TeamList : Screen("team_list")
    data object TeamDetails : Screen("team_details/{teamId}") {
        fun createRoute(teamId: Int) = "team_details/$teamId"
    }
    data object EditTeam : Screen("edit_team/{teamId}") {
        fun createRoute(teamId: Int) = "edit_team/$teamId"
    }
    data object Settings : Screen("settings")
}
