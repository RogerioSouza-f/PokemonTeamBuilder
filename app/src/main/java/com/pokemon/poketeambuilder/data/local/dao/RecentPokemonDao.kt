package com.pokemon.poketeambuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokemon.poketeambuilder.data.local.entity.RecentPokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentPokemonDao {

    @Query("SELECT * FROM recent_pokemon ORDER BY viewedAt DESC LIMIT 20")
    fun getRecentPokemon(): Flow<List<RecentPokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecent(recent: RecentPokemonEntity)

    @Query("DELETE FROM recent_pokemon")
    suspend fun clearRecent()
}
