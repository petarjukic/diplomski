package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Cart {
    var id: String? = null
    var name: String? = null
    var image: String? = null
    var price: Float? = null
    var quantity: Int = 0
    var totalPrice: Float = 0f
}