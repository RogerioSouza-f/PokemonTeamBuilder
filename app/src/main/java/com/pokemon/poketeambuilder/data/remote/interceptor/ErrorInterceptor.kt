package com.pokemon.poketeambuilder.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            chain.proceed(request)
        } catch (e: IOException) {
            throw IOException("Falha de conexão ao acessar: ${request.url}", e)
        }
    }
}
