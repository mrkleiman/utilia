package com.mariomora.utilia.core.storage

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Mario Mora - Android Dev
 * Clean Architecture - MVVM - UDF - SOLID
 */
interface LocalStorage {
    fun getString(key: String, defaultValue: String? = null): String?
    fun putString(key: String, value: String)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun remove(key: String)
    fun clear()
}

inline fun <reified T> LocalStorage.getObject(key: String): T? {
    val jsonString = getString(key) ?: return null
    return try {
        Json { ignoreUnknownKeys = true }.decodeFromString<T>(jsonString)
    } catch (_: Exception) {
        null
    }
}

inline fun <reified T> LocalStorage.putObject(key: String, value: T) {
    val jsonString = Json { ignoreUnknownKeys = true }.encodeToString(value)
    putString(key, jsonString)
}

class SharedPreferencesStorage(context: Context, name: String = "utilia_prefs") : LocalStorage {
    private val prefs: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    override fun getString(key: String, defaultValue: String?): String? = prefs.getString(key, defaultValue)

    override fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }
}
