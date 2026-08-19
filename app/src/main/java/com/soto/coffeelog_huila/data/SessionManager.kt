package com.soto.coffeelog_huila.data

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("coffeelog_prefs", Context.MODE_PRIVATE)

    fun saveUser(userId: Long, rol: String) {
        prefs.edit().putLong("user_id", userId).apply()
        prefs.edit().putString("user_rol", rol).apply()
        prefs.edit().putBoolean("is_logged", true).apply()
    }

    fun getRol(): String? = prefs.getString("user_rol", null)
    fun isLogged(): Boolean = prefs.getBoolean("is_logged", false)

    fun logout() {
        prefs.edit().clear().apply()
    }
}