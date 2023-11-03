package com.fxyandtjh.voiceaccounting.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.blankj.utilcode.util.Utils
import com.fxyandtjh.voiceaccounting.base.Constants
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SPSet<T>(private val key: String, private val defaultValue: T) : ReadWriteProperty<Any?, T> {
    companion object {
        val preference: SharedPreferences by lazy {
            Utils.getApp().getSharedPreferences(
                Constants.SP_NAME,
                Context.MODE_PRIVATE
            )
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun <T> saveValues(key: String, default: T) = with(preference.edit()) {
        when (default) {
            is Long -> putLong(key, default)
            is String -> putString(key, default)
            is Int -> putInt(key, default)
            is Boolean -> putBoolean(key, default)
            is Float -> putFloat(key, default)
            else -> throw IllegalArgumentException("This type of data can not be saved! ")
        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getSaveValue(key: String, default: T): T = with(preference) {
        val value: Any? = when (default) {
            is Long -> getLong(key, default)
            is String -> getString(key, default)
            is Int -> getInt(key, default)
            is Boolean -> getBoolean(key, default)
            is Float -> getFloat(key, default)
            else -> throw IllegalArgumentException("This type of data can not be saved! ")
        }
        value as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        return saveValues(key, value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getSaveValue(key, defaultValue)
    }
}