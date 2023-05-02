package com.example.diplomskirad.common.preferences

import android.content.Context

class LoginSharedPreferences(context: Context) : SharedPreferencesService(context) {
    private val PREFS_NAME = "LOGIN_INFO"
    private val EMAIL = "email"
    private val ROLE = "role"

    fun saveEmail(email: String) {
        save(PREFS_NAME, EMAIL, email)
    }

    fun saveRole(role: String) {
        save(PREFS_NAME, ROLE, role)
    }

    fun getEmail(): String? {
        return getString(PREFS_NAME, EMAIL, "")
    }

    fun getRole(): String? {
        return getString(PREFS_NAME, ROLE, "")
    }

    fun deleteLoginInfoForUser(username: String?) {
        erase(PREFS_NAME, username!!)
    }

    fun deleteRoleInfoForUser(role: String?) {
        erase(PREFS_NAME, role!!)
    }
}