/*
 *  Created by Grzegorz Małopolski on 10/13/21, 5:34 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.data.WindowSize
import com.ringpublishing.tracking.internal.factory.EventType
import com.ringpublishing.tracking.internal.util.buildToDX
import kotlin.math.roundToLong

internal class KeepAliveEventBuilder
{

	fun create(content: ContentMetadata?, keepAliveList: List<KeepAliveMetadata>): Event
	{
		val event = Event(AnalyticsSystem.TIMESCORE.text, EventType.KEEP_ALIVE.text)

		val windowSizes = mutableListOf<String>()
		val focusList = mutableListOf<Int>()
		val measureTypes = mutableListOf<String>()
		val timingsInSeconds = mutableListOf<Long>()
		val scrollOffsets = mutableListOf<Long>()

		keepAliveList.forEach { metaData ->
			with(metaData)
			{
				windowSizes.add(WindowSize(contentStatus.contentSize).toString())
				focusList.add(1)
				measureTypes.add(measureType.text)
				timingsInSeconds.add(timingInMillis / 1000L)
				scrollOffsets.add(contentStatus.scrollOffset.roundToLong())
			}
		}

		with(event)
		{
			content?.let { parameters["DX"] = content.buildToDX() }
			parameters["KTA"] = 1

			parameters["KDS"] = windowSizes.toTypedArray()
			parameters["KHF"] = focusList.toTypedArray()
			parameters["KMT"] = measureTypes.toTypedArray()
			parameters["KTP"] = timingsInSeconds.toTypedArray()
			parameters["KTS"] = scrollOffsets.toTypedArray()
		}

		return event
	}
}
