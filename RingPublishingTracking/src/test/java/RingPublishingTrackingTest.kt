package com.ringpublishing.tracking

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.delegate.RingPublishingTrackingDelegate
import com.ringpublishing.tracking.internal.api.ApiClient
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.ApiService
import com.ringpublishing.tracking.internal.service.EventsService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import java.net.URL

internal class RingPublishingTrackingTest
{

	@MockK
	lateinit var context: Application

	@MockK
	lateinit var resources: Resources

	@MockK
	lateinit var display: Display

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
	lateinit var displayManager: DisplayManager

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

		every { displayManager.getDisplay(Display.DEFAULT_DISPLAY) } returns display

		mockkStatic(Resources::class)
		every { Resources.getSystem().displayMetrics } returns displayMetrics

		every { context.getSharedPreferences(any(), any()) } returns sharedPreferences

		every { sharedPreferences.contains(any()) } returns true
		every { sharedPreferences.getString(any(), any()) } returns ""

		every { context.getSystemService(Context.DISPLAY_SERVICE) } returns displayManager
		every { context.getSystemService(Context.WINDOW_SERVICE) } returns windowManager

		every { context.packageName } returns "com.ringpublishing"

		every { ringPublishingTrackingConfiguration.apiKey } returns ""
		every { ringPublishingTrackingConfiguration.apiUrl } returns apiUrl
		every { ringPublishingTrackingConfiguration.applicationRootPath } returns ""
		every { ringPublishingTrackingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingTrackingConfiguration.applicationDefaultStructurePath } returns emptyList()

		RingPublishingTracking.initialize(context, ringPublishingTrackingConfiguration, ringPublishingTrackingDelegate)
	}
}
