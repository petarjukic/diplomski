package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product(
    val id: String,
    val productName: String,
    val price: Float,
    val categoryId: String,
    val image: String,
    val description: String? = null
)