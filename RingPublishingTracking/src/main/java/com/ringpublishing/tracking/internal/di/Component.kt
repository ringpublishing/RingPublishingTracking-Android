package com.ringpublishing.tracking.internal.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.ringpublishing.tracking.internal.ConfigurationManager

@SuppressLint("StaticFieldLeak")
internal object Component
{

    private lateinit var appApplication: Application

    var initialized = false

	private lateinit var configurationManager: ConfigurationManager

    fun initComponent(application: Application, configurationManager: ConfigurationManager)
    {
	    appApplication = application
	    this.configurationManager = configurationManager
        initOnStart()
	    initialized = true
    }

    private fun initOnStart()
    {
        provideAdvertisingInfo().obtainAdvertisingId()
    }

    fun provideContext(): Context = appApplication.applicationContext

	fun provideApplication() = appApplication

	fun provideConfigurationManager() = configurationManager
}
