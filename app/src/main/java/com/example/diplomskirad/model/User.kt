package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String,
    val userName: String,
    val email: String,
    val password: String,
    val age: Int,
    val address: String? = null,
    val role: String
)