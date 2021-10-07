package com.ringpublishing.tracking

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
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
	lateinit var ringPublishingTrackingConfiguration: RingPublishingTrackingConfiguration

	@MockK
	lateinit var ringPublishingTrackingDelegate: RingPublishingTrackingDelegate

	@MockK
	lateinit var apiUrl: URL

	@MockK
	lateinit var displayMetrics: DisplayMetrics

	@MockK
	lateinit var windowManager: WindowManager

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
		every { resources.configuration } returns configuration
		every { context.resources } returns resources
		every { context.resources.configuration } returns configuration

		every { context.resources.displayMetrics } returns displayMetrics
		every { context.getSharedPreferences(any(), any()) } returns sharedPreferences

		every { context.getSystemService(any()) } returns windowManager
		every { context.packageName } returns "com.ringpublishing"

		every { ringPublishingTrackingConfiguration.apiKey } returns ""
		every { ringPublishingTrackingConfiguration.apiUrl } returns apiUrl
		every { ringPublishingTrackingConfiguration.applicationRootPath } returns ""

		RingPublishingTracking.initialize(context, ringPublishingTrackingConfiguration, ringPublishingTrackingDelegate)
	}
}
