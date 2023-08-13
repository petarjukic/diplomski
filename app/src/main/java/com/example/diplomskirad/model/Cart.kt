package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Cart(
    var id: String? = null,
    var productId: String? = null,
    var name: String? = null,
    var image: String? = null,
    var price: Float? = null,
    var quantity: Int = 0,
    var totalPrice: Float = 0f,
    var userId: String? = null,
    var categoryId: String? = null,
)