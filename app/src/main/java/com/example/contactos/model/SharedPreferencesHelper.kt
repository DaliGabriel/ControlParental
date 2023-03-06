//Persistencia de datos pequeños

package com.example.contactos.model

import android.content.Context

class SharedPreferencesHelper {

    companion object {

        private const val SHARED_PREFERENCES_NAME = "MY_SHARED_PREFERENCES"

        // Guardar el valor de la variable en SharedPreferences
        fun saveContactosLeidos(context: Context, contactosLeidos: Boolean) {
            val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("contactosLeidos", contactosLeidos)
            editor.apply()
        }

        // Leer el valor de la variable de SharedPreferences
        fun getContactosLeidos(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return sharedPref.getBoolean("contactosLeidos", false)
        }

    }
}