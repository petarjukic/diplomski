package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val address: String? = null,
    val role: String
)