/*
 *  Created by Grzegorz Małopolski on 10/29/21, 10:22 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentSize
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.KeepAliveContentStatus
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewMetadata
import com.ringpublishing.tracking.internal.data.WindowSize
import com.ringpublishing.tracking.internal.decorator.EventDecorator
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewComponentSource
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewTriggerSource
import com.ringpublishing.tracking.internal.factory.EventType
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.EventsService
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL

internal class EventsReporterTest
{

	@MockK
	lateinit var eventService: EventsService

	@MockK
	lateinit var eventDecorator: EventDecorator

    @MockK
    lateinit var screenSizeInfo: ScreenSizeInfo

    @MockK
    lateinit var configurationManager: ConfigurationManager

	@MockK
	lateinit var event: Event

    private val sampleContentStatus = KeepAliveContentStatus(
        scrollOffsetPx = 0,
        contentSizePx = ContentSize(1000, 8000),
    )

    private val sampleContentMetadata = ContentMetadata(
        "publicationId",
        URL("https://domain.com"),
        "source System_Name",
        1,
        true,
        "my-unique-content-id-1234"
    )

    private val sampleEffectivePageViewMetadata = EffectivePageViewMetadata(
        componentSource = EffectivePageViewComponentSource.Other("audio"),
        triggerSource = EffectivePageViewTriggerSource.Other("play"),
        measurement = sampleContentStatus
    )

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)

        every { screenSizeInfo.getScreenSizePxFromMetrics() } returns WindowSize(1000, 2000)
        every { configurationManager.shouldReportEffectivePageViewEvent() } returns true

		Logger.debugLogEnabled(true)
	}

	@Test
	fun reportEvent_WhenReportEvent_ThenEventIsAddedToService()
	{
		every { eventDecorator.decorate(event) } returns event
        every { event.name } returns EventType.POLARIS.text

        val eventsReporter = EventsReporter(eventService, eventDecorator, screenSizeInfo, configurationManager)

		eventsReporter.reportEvent(event)

		verify { eventService.addEvent(event) }
	}

	@Test
	fun reportEvents()
	{
		every { eventDecorator.decorate(event) } returns event
		val eventsReporter = EventsReporter(eventService, eventDecorator, screenSizeInfo, configurationManager)

		eventsReporter.reportEvents(listOf(event, event))

		verify{ eventService.addEvents(listOf(event, event)) }
	}

    @Test
    fun whenSendingIsBlockedByConfiguration_thenShouldNotSendEvent() {
        val eventsReporter = EventsReporter(eventService, eventDecorator, screenSizeInfo, configurationManager)

        // Overriding configuration to simulate that sending of effective page view events is blocked
        every { configurationManager.shouldReportEffectivePageViewEvent() } returns false

        // Assert that the event should not be sent
        Assert.assertFalse(eventsReporter.shouldReportEPVEvent(sampleEffectivePageViewMetadata))
    }

    @Test
    fun whenScrollIsToLittle_thenShouldNotSentEvent() {
        val eventsReporter = EventsReporter(eventService, eventDecorator, screenSizeInfo, configurationManager)

        // Overriding sampleEffectivePageViewMetadata to simulate a scroll event with insufficient scroll offset
        val sampleEffectivePageViewMetadata = sampleEffectivePageViewMetadata.copy(
            componentSource = EffectivePageViewComponentSource.Scroll,
            triggerSource = EffectivePageViewTriggerSource.Scroll,
        )

        // Assert that the event should not be sent - scroll value 0 is to little - it is not >= 2 * screen height
        Assert.assertFalse(eventsReporter.shouldReportEPVEvent(sampleEffectivePageViewMetadata))
    }

    @Test
    fun whenScrollIsSufficient_thenShouldSendEvent() {
        val eventsReporter = EventsReporter(eventService, eventDecorator, screenSizeInfo, configurationManager)

        // Overriding sampleContentStatus with scroll offset that is sufficient for sending the event (2* screen height)
        val sampleContentStatus = sampleContentStatus.copy(scrollOffsetPx = 4000)

        // Overriding sampleEffectivePageViewMetadata to simulate a scroll event with sufficient scroll offset
        val sampleEffectivePageViewMetadata = sampleEffectivePageViewMetadata.copy(
            componentSource = EffectivePageViewComponentSource.Scroll,
            triggerSource = EffectivePageViewTriggerSource.Scroll,
            measurement = sampleContentStatus
        )

        // Assert that the event should not be sent - scroll value 4000 is enough - it is matching the >= 2 * screen height constraint
        Assert.assertTrue(eventsReporter.shouldReportEPVEvent(sampleEffectivePageViewMetadata))
    }

    @Test
    fun createEffectivePageViewEvent_thenShouldNotSendEventAgain_thenShouldSentAfterReset() {
        every { eventDecorator.decorate(event) } returns event
        every { event.name } returns EventType.POLARIS.text

        val eventsReporter = EventsReporter(eventService, eventDecorator, screenSizeInfo, configurationManager)

        // Assert that the event should be sent - this is the first call
        Assert.assertTrue(eventsReporter.shouldReportEPVEvent(sampleEffectivePageViewMetadata))

        // Report epv event, now isEventSent should be true and should not send again
        eventsReporter.reportEvent(event)

        // Assert that the event should not be sent again - isEventSent is now true
        Assert.assertFalse(eventsReporter.shouldReportEPVEvent(sampleEffectivePageViewMetadata))

        eventsReporter.resetEPVEventSentState()

        // Assert that the event should be sent - this is the first call after reset
        Assert.assertTrue(eventsReporter.shouldReportEPVEvent(sampleEffectivePageViewMetadata))
    }
}
