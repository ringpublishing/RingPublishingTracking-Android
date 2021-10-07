@file:Suppress("unused")

package com.ringpublishing.tracking.demo

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.delegate.RingPublishingTrackingDelegate
import com.ringpublishing.tracking.demo.logger.DemoAppLogger

class DemoApplication : MultiDexApplication()
{

	private val ringPublishingTrackingDelegate = object : RingPublishingTrackingDelegate
	{
		override fun ringPublishingTrackingDidAssignTrackingIdentifier(ringPublishingTracking: RingPublishingTracking, identifier: String)
		{
			Log.i("DemoApplication", "RingPublishingTracking: received tracking identifier:$identifier")
		}
	}

	override fun onCreate()
	{
		super.onCreate()

		// Initialize RingPublishingTracking module before your app wants to send any events.
		// The earlier you will do it - the earlier you will receive assigned 'trackingIdentifier' from SDK by 'RingPublishingTrackingDelegate'

		// Fill here your application module configuration
		val tenantId = "" // <YOUR_TENANT_ID>
		val apiKey = "" // <YOUR_API_KEY>
		// val apiUrl = URL("http://website.com") optional parameter to change api endpoint
		val applicationRootPath = "RingPublishingTrackingDemo"
		val applicationDefaultStructurePath = listOf("Default")
		val applicationDefaultAdvertisementArea = "DemoAdvertisementArea"

		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			tenantId = tenantId,
			apiKey = apiKey,
			applicationRootPath = applicationRootPath,
			applicationDefaultStructurePath = applicationDefaultStructurePath,
			applicationDefaultAdvertisementArea = applicationDefaultAdvertisementArea
		)

		// Optional enable debug mode, that print debug logs
		RingPublishingTracking.setDebugMode(BuildConfig.DEBUG)

		// Optional add own log receiver
		RingPublishingTracking.addLogListener(DemoAppLogger())

		RingPublishingTracking.initialize(this, ringPublishingTrackingConfiguration, ringPublishingTrackingDelegate)
	}
}
