package com.pokemon.poketeambuilder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pokemon.poketeambuilder.data.local.dao.FavoritePokemonDao
import com.pokemon.poketeambuilder.data.local.dao.RecentPokemonDao
import com.pokemon.poketeambuilder.data.local.dao.SearchHistoryDao
import com.pokemon.poketeambuilder.data.local.entity.FavoritePokemonEntity
import com.pokemon.poketeambuilder.data.local.entity.RecentPokemonEntity
import com.pokemon.poketeambuilder.data.local.entity.SearchHistoryEntity

@Database(
    entities = [
        FavoritePokemonEntity::class,
        RecentPokemonEntity::class,
        SearchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePokemonDao(): FavoritePokemonDao
    abstract fun recentPokemonDao(): RecentPokemonDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
