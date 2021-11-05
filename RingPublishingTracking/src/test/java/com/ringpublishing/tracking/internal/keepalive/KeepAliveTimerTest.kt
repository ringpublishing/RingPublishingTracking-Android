/*
 *  Created by Grzegorz Małopolski on 10/29/21, 2:29 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.internal.service.timer.KeepAliveSendTimerCallback
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class KeepAliveTimerTest
{
	@MockK
	lateinit var callback: KeepAliveSendTimerCallback

	lateinit var keepAliveTimer: KeepAliveTimer

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		RingPublishingTracking.setDebugMode(true)
		keepAliveTimer = KeepAliveTimer(callback)
	}

	@Test
	fun start_WhenStartTimer_ThenSendTimerCalledAfterTime()
	{
		keepAliveTimer.start()

		verify(timeout = 10000) { callback.onSendTimer() }
	}

	@Test
	fun start_WhenStartTimer_ThenActivityTimerCalledAfterTime()
	{
		keepAliveTimer.start()

		verify(timeout = 10000) { callback.onActivityTimer() }
	}

	@Test
	fun stop_WhenStartTimerAndStop_ThenNoSendCallback()
	{
		keepAliveTimer.start()
		keepAliveTimer.stop()

		verify(timeout = 10000, exactly = 0) { callback.onSendTimer() }
	}

	@Test
	fun stop_WhenStartTimerAndStop_ThenNoActivityTimerCallback()
	{
		keepAliveTimer.start()
		keepAliveTimer.stop()

		verify(timeout = 10000, exactly = 0) { callback.onActivityTimer() }
	}

	@Test
	fun scheduleSendTimer_WhenScheduled_ThenCallbackCalled()
	{
		keepAliveTimer.scheduleSendTimer()

		verify(timeout = 10000, exactly = 0) { callback.onSendTimer() }
	}

	@Test
	fun scheduleActivityTimer_WhenScheduled_ThenCallbackCalled()
	{
		keepAliveTimer.scheduleActivityTimer()

		verify(timeout = 10000, exactly = 0) { callback.onActivityTimer() }
	}

	@Test
	fun pause_WhenTimerPaused_ThenCallbacksNotCalled()
	{
		keepAliveTimer.start()
		keepAliveTimer.pause()

		verify(timeout = 10000, exactly = 0) { callback.onSendTimer() }
		verify(timeout = 10000, exactly = 0) { callback.onActivityTimer() }
	}

	@Test
	fun resume_WhenResumed_ThenCallbacksCalled()
	{
		keepAliveTimer.start()
		keepAliveTimer.pause()
		keepAliveTimer.resume()

		verify(timeout = 10000, exactly = 0) { callback.onSendTimer() }
		verify(timeout = 10000, exactly = 0) { callback.onActivityTimer() }
	}

	@Test
	fun pauseBackground_WhenPaused_ThenCallbacksNotCalled()
	{
		keepAliveTimer.start()
		keepAliveTimer.pauseBackground()

		verify(timeout = 10000, exactly = 0) { callback.onSendTimer() }
		verify(timeout = 10000, exactly = 0) { callback.onActivityTimer() }
	}

	@Test
	fun resumeBackground()
	{
		keepAliveTimer.start()
		keepAliveTimer.pauseBackground()

		verify(timeout = 10000, exactly = 0) { callback.onSendTimer() }
		verify(timeout = 10000, exactly = 0) { callback.onActivityTimer() }
	}

	@Test
	fun timeFromStartInForeground()
	{
		keepAliveTimer.start()
		keepAliveTimer.pause()

		Assert.assertNotNull(keepAliveTimer.timeFromStartInForeground())
	}
}
