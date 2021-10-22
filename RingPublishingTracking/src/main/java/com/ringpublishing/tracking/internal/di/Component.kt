package com.ringpublishing.tracking.internal.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

@SuppressLint("StaticFieldLeak")
internal object Component
{

    private lateinit var appApplication: Application

    fun initComponent(application: Application)
    {
	    appApplication = application
        initOnStart()
    }

    private fun initOnStart()
    {
        provideAdvertisingInfo().obtainAdvertisingId()
    }

    fun provideContext(): Context = appApplication.applicationContext

	fun provideApplication() = appApplication
}
