package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Category(
    val id: String? = null,
    val categoryName: String? = null,
    val removed: Boolean? = null
)
