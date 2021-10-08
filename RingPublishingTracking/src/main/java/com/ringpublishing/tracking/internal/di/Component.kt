package com.ringpublishing.tracking.internal.di

import android.annotation.SuppressLint
import android.app.Application

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

    fun provideContext() = appApplication.applicationContext

	fun provideApplication() = appApplication
}
