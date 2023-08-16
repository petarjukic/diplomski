package com.example.diplomskirad.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.diplomskirad.model.favorites.Favorites
import com.example.diplomskirad.model.favorites.dao.FavoritesDao

@Database(entities = [Favorites::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
}