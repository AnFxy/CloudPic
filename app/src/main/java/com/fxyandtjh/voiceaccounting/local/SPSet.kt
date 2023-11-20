package com.fxyandtjh.voiceaccounting.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.blankj.utilcode.util.Utils
import com.fxyandtjh.voiceaccounting.base.Constants
import com.fxyandtjh.voiceaccounting.viewmodel.JsonToBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SPSet<T>(private val key: String, private val defaultValue: T, private val type: Type = Any::class.java) : ReadWriteProperty<Any?, T> {
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
            else -> {
                val jsonSrc = Gson().toJson(default)
                putString(key, jsonSrc)
            }
        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getSaveValue(key: String, default: T, type: Type): T = with(preference) {
        val value: Any? = when (default) {
            is Long -> getLong(key, default)
            is String -> getString(key, default)
            is Int -> getInt(key, default)
            is Boolean -> getBoolean(key, default)
            is Float -> getFloat(key, default)
            else -> {
                val str = getString(key, "")
                str?.let {
                    Gson().fromJson(it, type)
                } ?: default
            }
        }
        value as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        return saveValues(key, value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getSaveValue(key, defaultValue, type)
    }
}