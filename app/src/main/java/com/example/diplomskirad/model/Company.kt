package com.example.diplomskirad.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Company(val id: String, val companyName: String)
