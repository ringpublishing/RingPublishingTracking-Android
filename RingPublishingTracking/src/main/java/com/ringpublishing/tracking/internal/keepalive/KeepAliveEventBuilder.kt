/*
 *  Created by Grzegorz Małopolski on 10/13/21, 5:34 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.device.WindowSizeInfo
import com.ringpublishing.tracking.internal.util.buildToDX

class KeepAliveEventBuilder(private val windowSizeInfo: WindowSizeInfo)
{

	fun create(contentMetadata: ContentMetadata?): Event
	{
		val event = Event()

		contentMetadata?.let { addContentInfoDX(event, it) }
		addContentSizeInfoKDS(event)
		addHasFocusKHF(event)
		addMeasureTypeKMT(event)
		addTabActiveKTA(event)
		addTimePointsKTP(event)
		addTopScrollKTS(event)

		return event
	}

	private fun addTopScrollKTS(event: Event)
	{
	}

	private fun addTimePointsKTP(event: Event)
	{
		TODO("Not yet implemented")
	}

	private fun addTabActiveKTA(event: Event)
	{
		TODO("Not yet implemented")
	}

	private fun addMeasureTypeKMT(event: Event)
	{
		TODO("Not yet implemented")
	}

	private fun addHasFocusKHF(event: Event)
	{
		TODO("Not yet implemented")
	}

	private fun addContentSizeInfoKDS(event: Event)
	{
		event.parameters["KDF"] = windowSizeInfo.getWindowSizeString()
	}

	private fun addContentInfoDX(event: Event, contentMetadata: ContentMetadata)
	{
		event.parameters["DX"] = contentMetadata.buildToDX()
	}
}
