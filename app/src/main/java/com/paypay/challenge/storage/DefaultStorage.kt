package com.paypay.challenge.storage

import android.content.Context
import androidx.core.content.edit
import okhttp3.internal.toImmutableMap
import kotlin.jvm.Throws

/**
 * For the purpose of interview test, this storage only supports data of types
 * - Boolean
 * - Int
 * - Float
 * - Long
 * - String
 * - Set<String>
 * Essentially, what SharedPreferences supports.
 * Modifying values is synchronous, so callers must handle asynchronicity themselves.
 */
internal class DefaultStorage(context: Context): Storage {
    private companion object {
        const val STORAGE_FILE = "DefaultStorage"
    }

    private val sharedPreferences = context.getSharedPreferences(STORAGE_FILE, Context.MODE_PRIVATE)

    @Throws(IllegalArgumentException::class)
    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    override fun <T> get(key: String, default: T): T {
        return when (default) {
            is Boolean -> sharedPreferences.getBoolean(key, default)
            is Int -> sharedPreferences.getInt(key, default)
            is Float -> sharedPreferences.getFloat(key, default)
            is Long -> sharedPreferences.getLong(key, default)
            is String -> sharedPreferences.getString(key, default)
            is Set<*> -> sharedPreferences.getStringSet(key, default as Set<String>)
            else -> throw IllegalArgumentException("Type not supported")
        } as T
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): T? {
        return if (sharedPreferences.contains(key)) {
            sharedPreferences.all[key] as? T
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getAll(): Map<String, *> {
        return sharedPreferences.all.toImmutableMap()
    }

    override fun set(key: String, value: Boolean) {
        sharedPreferences.edit(commit = true) {
            putBoolean(key, value)
        }
    }

    override fun set(key: String, value: Int) {
        sharedPreferences.edit(commit = true) {
            putInt(key, value)
        }
    }

    override fun set(key: String, value: Float) {
        sharedPreferences.edit(commit = true) {
            putFloat(key, value)
        }
    }

    override fun set(key: String, value: Long) {
        sharedPreferences.edit(commit = true) {
            putLong(key, value)
        }
    }

    override fun set(key: String, value: String) {
        sharedPreferences.edit(commit = true) {
            putString(key, value)
        }
    }

    override fun set(key: String, value: Set<String>) {
        sharedPreferences.edit(commit = true) {
            putStringSet(key, value)
        }
    }

    override fun remove(key: String) {
        sharedPreferences.edit(commit = true) {
            remove(key)
        }
    }
}