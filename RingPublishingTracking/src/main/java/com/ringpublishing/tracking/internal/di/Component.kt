package com.ringpublishing.tracking.internal.di

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
internal object Component
{

    private lateinit var context: Context

    fun initComponent(appContext: Context)
    {
        context = appContext.applicationContext
        initOnStart()
    }

    private fun initOnStart()
    {
        provideAdvertisingInfo().obtainAdvertisingId()
    }

    fun provideContext() = context
}
