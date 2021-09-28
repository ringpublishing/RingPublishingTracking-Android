package com.ringpublishing.tracking

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration

import android.content.res.Resources
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.delegate.RingPublishingTrackingDelegate

import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.net.URL

class RingPublishingTrackingTest
{

	@MockK
	lateinit var context: Context

	@MockK
	lateinit var resources: Resources

	@MockK
	lateinit var packageManager: PackageManager

	@MockK
	lateinit var configuration: Configuration

	@MockK
	lateinit var sharedPreferences: SharedPreferences

	@MockK
	lateinit var RingPublishingTrackingConfiguration: RingPublishingTrackingConfiguration

	@MockK
	lateinit var RingPublishingTrackingDelegate: RingPublishingTrackingDelegate

	@MockK
	lateinit var apiUrl: URL

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(false)
	}

	@Test
	fun initialize_WhenCorrectParametersSet_ThenInitializedCorrectly()
	{
		every { context.applicationContext } returns context
		every { context.packageManager } returns packageManager
		every { packageManager.queryIntentServices(any(), any()) } returns emptyList()
		every { context.resources } returns resources
		every { resources.configuration } returns configuration
		every { context.getSharedPreferences(any(), any()) } returns sharedPreferences

		every { RingPublishingTrackingConfiguration.apiKey } returns ""
		every { RingPublishingTrackingConfiguration.apiUrl } returns apiUrl

		RingPublishingTracking.initialize(context, RingPublishingTrackingConfiguration, RingPublishingTrackingDelegate)
	}
}
