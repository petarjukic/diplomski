package com.example.diplomskirad.model.favorites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favorites(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "product_id") val productId: String?
)