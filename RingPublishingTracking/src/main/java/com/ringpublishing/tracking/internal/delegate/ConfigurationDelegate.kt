package com.ringpublishing.tracking.internal.delegate

import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.config.OperationMode
import com.ringpublishing.tracking.internal.log.Logger

internal class ConfigurationDelegate
{

	internal lateinit var ringPublishingTrackingConfiguration: RingPublishingTrackingConfiguration

	private val operationMode = OperationMode()

	fun initializeConfiguration(ringPublishingTrackingConfiguration: RingPublishingTrackingConfiguration)
	{
		this.ringPublishingTrackingConfiguration = ringPublishingTrackingConfiguration
		Logger.debug("Initialize configuration $ringPublishingTrackingConfiguration")
	}

	fun setDebugMode(enabled: Boolean)
	{
		operationMode.debugEnabled = enabled
		Logger.info("Set debug mode enabled: $enabled")
		Logger.debugLogEnabled(enabled)
	}

	fun setOptOutMode(enabled: Boolean)
	{
		operationMode.optOutEnabled = enabled
		Logger.info("Set Opt out mode enabled $enabled")
	}

	fun isOptOutModeEnabled() = operationMode.optOutEnabled
}
