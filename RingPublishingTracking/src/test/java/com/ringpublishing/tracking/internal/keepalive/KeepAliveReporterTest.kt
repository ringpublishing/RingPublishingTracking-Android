/*
 *  Created by Grzegorz Małopolski on 10/26/21, 11:58 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentSize
import com.ringpublishing.tracking.data.KeepAliveContentStatus
import com.ringpublishing.tracking.internal.EventsReporter
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

internal class KeepAliveReporterTest
{

	@MockK
	lateinit var eventsReporter: EventsReporter

	@MockK
	lateinit var screenSizeInfo: ScreenSizeInfo

	@MockK
	lateinit var contentMetadata: ContentMetadata

	@MockK
	lateinit var keepAliveDataSource: KeepAliveDataSource

	private lateinit var lifecycleOwner: LifecycleOwner

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		RingPublishingTracking.setDebugMode(true)

		every { screenSizeInfo.getWindowSizeDpString(any()) } returns "1x1"
		every { keepAliveDataSource.didAskForKeepAliveContentStatus(any()) } returns KeepAliveContentStatus(1, ContentSize(2, 2))

		every { keepAliveDataSource.toString() } returns ""
		every { screenSizeInfo.getSizeDp(any()) } returns 1
		every { contentMetadata.contentWasPaidFor } returns false
		every { contentMetadata.sourceSystemName } returns ""
		every { contentMetadata.publicationId } returns ""
		every { contentMetadata.contentPartIndex } returns 1
		every { contentMetadata.contentId } returns "1"

		every { keepAliveDataSource.toString() } returns ""

		val cycle = object : Lifecycle()
		{
            override val currentState: State
                get() = State.CREATED

            override fun addObserver(observer: LifecycleObserver)
			{
			}

			override fun removeObserver(observer: LifecycleObserver)
			{
			}

		}

        lifecycleOwner = object : LifecycleOwner {
            override val lifecycle: Lifecycle
                get() = cycle
        }
	}

	@Test
	fun start_WhenStartedCollectingEvents_ThenEventAfterTimeIsSend()
	{

		val keepAliveReporter = KeepAliveReporter(eventsReporter, screenSizeInfo, lifecycleOwner)

		keepAliveReporter.start(contentMetadata, keepAliveDataSource, false)

		verify(timeout = 10000) { eventsReporter.reportEvent(any()) }
	}

	@Test
	fun pause_WhenCollectingEventsPaused_ThenAnyEventSend()
	{
		val keepAliveReporter = KeepAliveReporter(eventsReporter, screenSizeInfo, lifecycleOwner)

		keepAliveReporter.start(contentMetadata, keepAliveDataSource, false)

		keepAliveReporter.pause()

		verify(timeout = 10000, exactly = 0) { eventsReporter.reportEvent(any()) }
	}

	@Test
	fun resume_WhenReportStartedAndPaused_ThenResumeWillSendCollectedEvents()
	{
		val keepAliveReporter = KeepAliveReporter(eventsReporter, screenSizeInfo, lifecycleOwner)

		keepAliveReporter.start(contentMetadata, keepAliveDataSource, false)

		keepAliveReporter.pause()
		keepAliveReporter.resume()

		verify(timeout = 10000, exactly = 0) { eventsReporter.reportEvent(any()) }
	}

	@Test
	fun stop_WhenStartedAndEventsCollected_ThenSendEventsBeforeStop()
	{
		val keepAliveReporter = KeepAliveReporter(eventsReporter, screenSizeInfo, lifecycleOwner)

		keepAliveReporter.start(contentMetadata, keepAliveDataSource, false)

		Thread.sleep(5000)
		keepAliveReporter.stop()

		verify(timeout = 10000, atLeast = 1) { eventsReporter.reportEvent(any()) }
	}
}
