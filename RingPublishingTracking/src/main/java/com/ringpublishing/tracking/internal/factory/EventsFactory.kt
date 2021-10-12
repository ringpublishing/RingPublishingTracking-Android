/*
 *  Created by Grzegorz Małopolski on 10/11/21, 1:14 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import java.net.URL

class EventsFactory
{

	fun createClickEvent(selectedEventName: String? = null, publicationUrl: URL? = null): Event
	{
		val parameters = mutableMapOf<String, Any>()

		selectedEventName?.let { parameters[UserEventParam.SELECTED_ELEMENT_NAME.text] = it }

		publicationUrl?.let { parameters[UserEventParam.TARGET_URL.text] = it.toString() }

		return Event(AnalyticsSystem.KROPKA_EVENTS.text, EventType.CLICK.text, parameters)
	}
}
