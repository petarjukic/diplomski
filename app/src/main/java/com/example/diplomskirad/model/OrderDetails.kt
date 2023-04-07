package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class OrderDetails(val orderId: String? = null, val userId: String? = null, val total: Float? = null)
