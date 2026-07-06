package com.pokemon.poketeambuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokemon.poketeambuilder.data.local.entity.FavoritePokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePokemonDao {

    @Query("SELECT * FROM favorite_pokemon ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoritePokemonEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_pokemon WHERE pokemonId = :pokemonId)")
    fun isFavorite(pokemonId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoritePokemonEntity)

    @Delete
    suspend fun removeFavorite(favorite: FavoritePokemonEntity)

    @Query("DELETE FROM favorite_pokemon WHERE pokemonId = :pokemonId")
    suspend fun removeFavoriteById(pokemonId: Int)
}
