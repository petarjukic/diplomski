package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class OrderDetails(val orderId: String, val userId: String, val total: Float? = null)
