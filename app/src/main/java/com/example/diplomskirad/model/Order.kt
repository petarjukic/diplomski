package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Order(val id: String, val orderDate: Date, val userId: String, val payed: Boolean? = null)