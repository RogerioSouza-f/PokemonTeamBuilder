package com.pokemon.poketeambuilder.di

import android.content.Context
import androidx.room.Room
import com.pokemon.poketeambuilder.data.local.AppDatabase
import com.pokemon.poketeambuilder.data.local.dao.FavoritePokemonDao
import com.pokemon.poketeambuilder.data.local.dao.RecentPokemonDao
import com.pokemon.poketeambuilder.data.local.dao.SearchHistoryDao
import com.pokemon.poketeambuilder.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideFavoritePokemonDao(db: AppDatabase): FavoritePokemonDao = db.favoritePokemonDao()

    @Provides
    fun provideRecentPokemonDao(db: AppDatabase): RecentPokemonDao = db.recentPokemonDao()

    @Provides
    fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()
}
