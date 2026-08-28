package com.soto.coffeelog_huila.data

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("coffeelog_prefs", Context.MODE_PRIVATE)

    fun saveUser(userId: Long, rol: String, nombre: String) {
        prefs.edit().apply {
            putLong("user_id", userId)
            putString("user_rol", rol)
            putString("user_nombre", nombre)
            putBoolean("is_logged", true)
            apply()
        }
    }

    fun getRol(): String? = prefs.getString("user_rol", null)

    fun getNombre(): String = prefs.getString("user_nombre", "Productor") ?: "Productor"

    // AQUÍ ESTÁ LA FUNCIÓN QUE FALTABA
    fun getUserId(): Long = prefs.getLong("user_id", -1L)

    fun isLogged(): Boolean = prefs.getBoolean("is_logged", false)

    fun logout() {
        prefs.edit().clear().apply()
    }
}