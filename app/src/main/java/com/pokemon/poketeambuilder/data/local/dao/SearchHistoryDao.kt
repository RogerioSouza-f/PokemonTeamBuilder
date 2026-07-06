package com.pokemon.poketeambuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pokemon.poketeambuilder.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT 10")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert
    suspend fun addSearch(search: SearchHistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}
