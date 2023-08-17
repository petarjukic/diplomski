package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product(
    val id: String? = null,
    val productName: String? = null,
    val price: Float? = null,
    val categoryId: String? = null,
    val image: String? = null,
    val description: String? = null,
    val companyId: String? = null,
    val addedToFavorites: Boolean? = null
)