package com.example.diplomskirad.service_manager.user_manager

import javax.inject.Singleton

@Singleton
class UserManager {

    var isLoggedIn = false
        set(value) {
            field = value
            if (!value) {
                email = null
                role = null
            }
        }

    var username: String? = null
        private set
    var email: String? = null
        private set
    var role: String? = null
        private set

    fun setUser(
        email: String?,
        role: String
    ) {
        this.email = email
        this.role = role
    }
}