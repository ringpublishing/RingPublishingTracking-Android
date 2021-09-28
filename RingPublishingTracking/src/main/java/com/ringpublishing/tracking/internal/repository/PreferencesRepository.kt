package com.ringpublishing.tracking.internal.repository

import android.content.Context
import com.google.gson.Gson
import java.lang.reflect.Type

internal class PreferencesRepository(context: Context, private val gson: Gson) : DataRepository
{
    private val name = "eventsPreferences"

    private val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    override fun <T> saveObject(key: String, value: T) = saveString(key, gson.toJson(value))

    override fun <T> readObject(key: String, type: Type): T?
    {
        val stringObject = readString(key) ?: return null
        return gson.fromJson(stringObject, type)
    }
    override fun readString(key: String): String?
    {
        if (!preferences.contains(key)) return null
        return preferences.getString(key, null)
    }

    override fun saveString(key: String, value: String) = preferences.edit().putString(key, value).apply()

    override fun readLong(key: String): Long?
    {
        if (!preferences.contains(key)) return null

        return preferences.getLong(key, 0)
    }

    override fun saveLong(key: String, value: Long) = preferences.edit().putLong(key, value).apply()

    override fun remove(key: String) = preferences.edit().remove(key).apply()

    override fun removeAllWithPrefix(key: String)
    {
        val edit = preferences.edit()
        preferences.all.keys.filter { it.startsWith(key) }.forEach { edit.remove(it) }
        edit.apply()
    }
}
