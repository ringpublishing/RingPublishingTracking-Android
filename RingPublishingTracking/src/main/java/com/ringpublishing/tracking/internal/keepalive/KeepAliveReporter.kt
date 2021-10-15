/*
 *  Created by Grzegorz Małopolski on 10/13/21, 5:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.EventsReporter
import com.ringpublishing.tracking.internal.device.WindowSizeInfo
import com.ringpublishing.tracking.internal.service.timer.KeepAliveSendTimerCallback
import java.util.concurrent.CopyOnWriteArrayList

internal class KeepAliveReporter(
	private val eventsReporter: EventsReporter,
	windowSizeInfo: WindowSizeInfo,
) : KeepAliveSendTimerCallback
{

	private val keepAliveSendTimer = KeepAliveSendTimer(this)

	private val events = CopyOnWriteArrayList<Event>()

	private val eventBuilder = KeepAliveEventBuilder(windowSizeInfo)

	fun stop()
	{
		keepAliveSendTimer.stop()
	}

	fun pause()
	{
		keepAliveSendTimer.pause()
	}

	fun resume()
	{
		keepAliveSendTimer.start()
	}

	@Synchronized
	override fun send()
	{
		eventsReporter.reportEvents(events)
		events.clear()
	}

	override fun addNewEvent()
	{
		val event = createEvent()
		events.add(event)
	}

	private fun createEvent(): Event
	{
		return eventBuilder.create()
	}
}
