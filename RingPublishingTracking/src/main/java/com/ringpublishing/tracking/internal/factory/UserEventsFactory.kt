/*
 *  Created by Grzegorz Małopolski on 10/7/21, 4:36 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.data.EventName

class UserEventsFactory : EventFactory
{

	override fun create(eventName: EventName): Event
	{
		return when (eventName)
		{
			EventName.PAGE_VIEW ->
			{
				createPageView()
			}
			EventName.CONTENT_PAGE_VIEW ->
			{
				createContentPageView()
			}
		}
	}

	private fun createPageView(): Event
	{
		return Event("todo", EventName.CONTENT_PAGE_VIEW.text)
	}

	private fun createContentPageView(): Event
	{
		return Event("todo", EventName.PAGE_VIEW.text)
	}
}
