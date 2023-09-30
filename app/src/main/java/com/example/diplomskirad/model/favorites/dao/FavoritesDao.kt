package com.example.diplomskirad.model.favorites.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.diplomskirad.model.favorites.Favorites

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites")
    fun getAll(): List<Favorites>

    @Insert
    fun insertAll(vararg favorites: Favorites)

    @Query("delete from favorites where product_id LIKE :productId")
    fun delete(productId: String)
}