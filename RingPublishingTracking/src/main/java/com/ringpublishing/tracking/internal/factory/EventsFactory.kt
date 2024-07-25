/*
 *  Created by Grzegorz Małopolski on 10/11/21, 1:14 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.decorator.EventParam
import com.ringpublishing.tracking.internal.decorator.createMarkedAsPaidParam
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.util.buildToDX
import java.net.URL

class EventsFactory(private val gson: Gson)
{

	fun createClickEvent(selectedEventName: String? = null, publicationUrl: URL? = null, contentIdentifier: String? = null): Event
	{
		val parameters = mutableMapOf<String, Any>()

		selectedEventName?.let { parameters[UserEventParam.SELECTED_ELEMENT_NAME.text] = it }

		publicationUrl?.let { parameters[UserEventParam.TARGET_URL.text] = it.toString() }

		contentIdentifier?.let { parameters[UserEventParam.PAGE_VIEW_RESOURCE_IDENTIFIER.text] = it }

		return Event(AnalyticsSystem.KROPKA_EVENTS.text, EventType.CLICK.text, parameters)
	}

	fun createUserActionEvent(actionName: String, actionSubtypeName: String, parametersString: String? = null, parametersMap: Map<String, Any>? = null): Event
	{
		val parameters = mutableMapOf<String, Any>()

		parameters[UserEventParam.USER_ACTION_CATEGORY_NAME.text] = actionName
		parameters[UserEventParam.USER_ACTION_SUBTYPE_NAME.text] = actionSubtypeName

		var payload: String? = parametersString

		parametersMap?.let {
			try
			{
				payload = gson.toJson(parametersMap)
			} catch (e: JsonParseException)
			{
				Logger.warn("Ignore payload $parametersMap. Parse exception $e")
			}
		}

		payload?.let { parameters[UserEventParam.USER_ACTION_PAYLOAD.text] = String(it.encodeToByteArray(), Charsets.UTF_8) }

		return Event(AnalyticsSystem.KROPKA_EVENTS.text, EventType.USER_ACTION.text, parameters)
	}

	fun createPageViewEvent(contentIdentifier: String? = null, contentMetadata: ContentMetadata? = null): Event
	{
        val parameters = mutableMapOf<String, Any>()

        contentIdentifier?.let {
            parameters[UserEventParam.PAGE_VIEW_RESOURCE_IDENTIFIER.text] = it
        }
        contentMetadata?.let { metadata ->
            parameters[UserEventParam.PAGE_VIEW_CONTENT_INFO.text] = metadata.buildToDX()
            createMarkedAsPaidParam(gson, metadata)?.let { param -> parameters[EventParam.MARKED_AS_PAID_DATA.text] = param }
        }

        return Event(AnalyticsSystem.KROPKA_STATS.text, EventType.PAGE_VIEW.text, parameters)
	}
}
