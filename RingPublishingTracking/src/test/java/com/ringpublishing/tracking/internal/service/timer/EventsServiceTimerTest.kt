/*
 *  Created by Grzegorz Małopolski on 10/28/21, 3:46 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.service.timer

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.internal.service.timer.EventServiceTimerCallback
import com.ringpublishing.tracking.internal.service.timer.EventsServiceTimer
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class EventsServiceTimerTest
{

	@MockK
	internal lateinit var eventServiceTimerCallback: EventServiceTimerCallback

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		RingPublishingTracking.setDebugMode(true)
	}

	@Test
	fun scheduleFlush_NoFlush_NoCallBack()
	{
		val eventsServiceTimer = EventsServiceTimer()
		eventsServiceTimer.postInterval = 1000

		eventsServiceTimer.flushCallback = eventServiceTimerCallback

		verify(exactly = 0) { eventServiceTimerCallback.readyToFlush() }
	}

	@Test
	fun scheduleFlush_OneFlush_OneCallBack()
	{
		val eventsServiceTimer = EventsServiceTimer()
		eventsServiceTimer.postInterval = 1000

		eventsServiceTimer.flushCallback = eventServiceTimerCallback
		eventsServiceTimer.scheduleFlush()

		verify { eventServiceTimerCallback.readyToFlush() }
	}

	@Test
	fun scheduleFlush_TwoFlush_OneCallBack()
	{
		val eventsServiceTimer = EventsServiceTimer()
		eventsServiceTimer.postInterval = 1000

		eventsServiceTimer.flushCallback = eventServiceTimerCallback
		eventsServiceTimer.scheduleFlush()
		eventsServiceTimer.scheduleFlush()

		verify(atMost = 1) { eventServiceTimerCallback.readyToFlush() }
	}

	@Test
	fun scheduleFlush_TwoFlushWIthDelay_TwoCallBack()
	{
		val eventsServiceTimer = EventsServiceTimer()
		eventsServiceTimer.postInterval = 1000

		eventsServiceTimer.flushCallback = eventServiceTimerCallback
		eventsServiceTimer.scheduleFlush()
		Thread.sleep(3000)
		eventsServiceTimer.scheduleFlush()

		verify(atMost = 2) { eventServiceTimerCallback.readyToFlush() }
	}

	@Test
	fun scheduleFlush_ScheduleIsLargerThanInterval_AllCallsBack()
	{
		val eventsServiceTimer = EventsServiceTimer()
		eventsServiceTimer.postInterval = 1000

		eventsServiceTimer.flushCallback = eventServiceTimerCallback
		eventsServiceTimer.scheduleFlush()
		Thread.sleep(3000)
		eventsServiceTimer.scheduleFlush()
		Thread.sleep(3000)
		eventsServiceTimer.scheduleFlush()
		Thread.sleep(3000)
		eventsServiceTimer.scheduleFlush()
		Thread.sleep(3000)
		eventsServiceTimer.scheduleFlush()

		verify(exactly = 5, timeout = 14000) { eventServiceTimerCallback.readyToFlush() }
	}

	@Test
	fun scheduleFlush_ScheduleIsSmallerThanInterval_AllCallsBack()
	{
		val eventsServiceTimer = EventsServiceTimer()
		eventsServiceTimer.postInterval = 1000

		eventsServiceTimer.flushCallback = eventServiceTimerCallback
		eventsServiceTimer.scheduleFlush()
		Thread.sleep(100)
		eventsServiceTimer.scheduleFlush()
		Thread.sleep(100)
		eventsServiceTimer.scheduleFlush()
		Thread.sleep(100)
		eventsServiceTimer.scheduleFlush()
		Thread.sleep(100)
		eventsServiceTimer.scheduleFlush()

		verify(atLeast = 2, timeout = 1000) { eventServiceTimerCallback.readyToFlush() }
	}
}
