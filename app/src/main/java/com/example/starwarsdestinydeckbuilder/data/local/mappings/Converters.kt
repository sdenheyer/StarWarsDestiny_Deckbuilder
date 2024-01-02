package com.example.starwarsdestinydeckbuilder.data.local.mappings

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun <T> toString(value: T?): String? {
        val gson = Gson()
        val json = gson.toJson(value)
        return json
    }

    @TypeConverter
    fun <T> fromString(value: String?): T? {
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(value, type)
    }

   /* @TypeConverter
    fun fromList(value: List<String>?): String? {
        val gson = Gson()
        val json = gson.toJson(value)
        return json
    }

    @TypeConverter
    fun listToString(value: String?): List<String>? {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromMap(value: Map<String, String>?): String? {
        val gson = Gson()
        val json = gson.toJson(value)
        return json
    }

    @TypeConverter
    fun mapToString(value: String?): Map<String, String>? {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromMapofArray(value: Map<String, Array<String>>?): String? {
        val gson = Gson()
        val json = gson.toJson(value)
        return json
    }

    @TypeConverter
    fun MapofArrayToString(value: String?): Map<String, Array<String>>? {
        val type = object : TypeToken<Map<String, Array<String>>>() {}.type
        return Gson().fromJson(value, type)
    }*/
}