package com.ringpublishing.tracking.internal.di

import com.google.gson.FieldNamingPolicy
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.DataRepository
import com.ringpublishing.tracking.internal.repository.PreferencesRepository
import com.ringpublishing.tracking.internal.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder

private val gson: Gson by lazy { GsonBuilder().create() }

private val snakeCaseGson: Gson by lazy {
    GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
}

internal fun Component.provideGson() = gson

internal fun Component.provideSnakeCaseGson() = snakeCaseGson

private val dataRepository: DataRepository by lazy { PreferencesRepository(Component.provideContext(), Component.provideGson()) }

internal fun Component.provideDataRepository() = dataRepository

private val apiRepository: ApiRepository by lazy { ApiRepository(Component.provideDataRepository()) }

internal fun Component.provideApiRepository() = apiRepository

private val userRepository: UserRepository by lazy {
    UserRepository(
	    Component.provideContext(),
        Component.provideAdvertisingInfo(),
        Component.provideDeviceInfo(),
        Component.provideDataRepository()
    )
}

internal fun Component.provideUserRepository() = userRepository
