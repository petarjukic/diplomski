package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Order(
    val id: String? = null,
    val orderDate: Date? = null,
    val userId: String? = null,
    val payed: Boolean? = null
)