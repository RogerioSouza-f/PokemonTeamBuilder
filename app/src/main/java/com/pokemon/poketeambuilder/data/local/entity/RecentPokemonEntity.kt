package com.pokemon.poketeambuilder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "recent_pokemon")
data class RecentPokemonEntity(
    @PrimaryKey val pokemonId: Int,
    val name: String,
    val imageUrl: String,
    val viewedAt: Long = System.currentTimeMillis()
)
