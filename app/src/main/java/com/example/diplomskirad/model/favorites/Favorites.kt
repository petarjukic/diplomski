package com.example.diplomskirad.model.favorites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorites(
    @ColumnInfo(name = "product_id") val productId: String?,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)