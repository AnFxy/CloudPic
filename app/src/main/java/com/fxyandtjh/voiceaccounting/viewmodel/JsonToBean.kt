package com.fxyandtjh.voiceaccounting.viewmodel

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.reflect.Type


class JsonToBean {
    inline fun <reified T> Gson.fromJson2(json: String, type: Type): T? {
        val jsonObject = JSONObject(json)
        val data = jsonObject.getString("data")
        return if (data.isEmpty()) {
            null
        } else {
            fromJson(data, type)
        }
    }

    inline fun <reified T> toBean(json: String): T? {
        val typeT = object : TypeToken<T>() {}.type
        return Gson ().fromJson2<T>(json, typeT)
    }
}