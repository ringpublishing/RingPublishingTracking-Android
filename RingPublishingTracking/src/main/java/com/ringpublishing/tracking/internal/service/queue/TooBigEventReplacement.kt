/*
 *  Created by Grzegorz Małopolski on 11/2/21, 4:48 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.service.queue

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.factory.EventType
import com.ringpublishing.tracking.internal.log.Logger

internal class TooBigEventReplacement(private val configurationManager: ConfigurationManager)
{

	fun replace(event: Event, eventSize: Long): Event
	{
		val parameters = mutableMapOf<String, Any>()

		val applicationName = "${configurationManager.getRootPath()}${Constants.defaultRootPathSuffix}"

		parameters["VE"] = "AppError"
		parameters["VM"] = "Application $applicationName tried to send event (name: ${event.name}, size: $eventSize, reason: exceeding size limit)."

		Logger.warn("TooBigEventReplacement: Replace too big event to error event. Event replaced is: $event")
		return Event(AnalyticsSystem.KROPKA_MONITORING.text, EventType.ERROR.text, parameters)
	}
}
