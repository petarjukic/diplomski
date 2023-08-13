package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SoldItems(
    val id: String? = null,
    val productName: String? = null,
    val price: Float? = null,
    val categoryId: String? = null,
    val image: String? = null,
    var count: Int? = null
)