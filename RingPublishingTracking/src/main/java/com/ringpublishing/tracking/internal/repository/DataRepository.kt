package com.ringpublishing.tracking.internal.repository

import java.lang.reflect.Type

internal interface DataRepository
{
    fun <T> readObject(key: String, type: Type): T?

    fun <T> saveObject(key: String, value: T)

    fun readString(key: String): String?

    fun saveString(key: String, value: String)

    fun readLong(key: String): Long?

    fun saveLong(key: String, value: Long)

    fun remove(key: String)

    fun removeAllWithPrefix(key: String)
}
