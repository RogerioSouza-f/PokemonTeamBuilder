package com.pokemon.poketeambuilder.network

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val error: AppError) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

sealed class AppError(val message: String) {
    data object NoInternet : AppError("Sem conexão com a internet. Verifique sua rede.")
    data object PokemonNotFound : AppError("Pokémon não encontrado. Verifique o nome ou número digitado.")
    data class ApiError(val code: Int, val apiMessage: String) :
        AppError("Erro da API ($code): $apiMessage")
    data class UnknownError(val throwableMessage: String?) :
        AppError("Erro interno inesperado: ${throwableMessage ?: "desconhecido"}")
}
