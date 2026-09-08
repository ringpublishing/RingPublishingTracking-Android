package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentSize
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.KeepAliveContentStatus
import com.ringpublishing.tracking.internal.EventsReporter
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewMetadata
import com.ringpublishing.tracking.internal.factory.EffectivePageViewEventFactory
import com.ringpublishing.tracking.internal.keepalive.KeepAliveReporter
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.URL

internal class EffectivePageViewTest
{
    private val eventsReporter = mockk<EventsReporter>()
    private val keepAliveReporter = mockk<KeepAliveReporter>()
    private val effectivePageViewEventFactory = mockk<EffectivePageViewEventFactory>()

    @Before
    fun setUp()
    {
        RingPublishingTracking.eventsReporter = eventsReporter
        RingPublishingTracking.keepAliveReporter = keepAliveReporter
        RingPublishingTracking.effectivePageViewEventFactory = effectivePageViewEventFactory
        setInitialized(true)
    }

    @After
    fun tearDown()
    {
        setInitialized(false)
    }

    @Test
    fun reportEffectivePageView_WithoutLastContentStatus_ReportsEventWithZeroMeasurement()
    {
        val contentMetadata = ContentMetadata(
            publicationId = "publication-id",
            publicationUrl = URL("https://example.com/article"),
            sourceSystemName = "cms",
            paidContent = false,
            contentId = "content-id",
            contentSpaceUuid = "content-space-uuid",
        )
        val metadata = slot<EffectivePageViewMetadata>()
        val event = Event()
        every { keepAliveReporter.lastContentStatus } returns null
        every { eventsReporter.shouldReportEPVEvent(capture(metadata)) } returns true
        every { effectivePageViewEventFactory.create(contentMetadata, any()) } returns event
        every { eventsReporter.reportEvent(event) } just Runs

        RingPublishingTracking.reportEffectivePageView(
            contentMetadata = contentMetadata,
            effectivePageViewComponentSource = "audio",
            effectivePageViewTriggerSource = "play",
        )

        assertEquals(
            KeepAliveContentStatus(scrollOffsetPx = 0, contentSizePx = ContentSize(0, 0)),
            metadata.captured.measurement,
        )
        verify(exactly = 1) { eventsReporter.reportEvent(event) }
    }

    private fun setInitialized(value: Boolean)
    {
        RingPublishingTracking::class.java.getDeclaredField("isInitialized").apply {
            isAccessible = true
            setBoolean(null, value)
        }
    }
}
