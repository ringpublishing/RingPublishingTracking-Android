package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.data.ContentViewType
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.delegate.RingPublishingTrackingKeepAliveDataSource
import com.ringpublishing.tracking.internal.EventsReporter
import com.ringpublishing.tracking.internal.decorator.EventParam
import com.ringpublishing.tracking.internal.keepalive.KeepAliveReporter
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.ref.WeakReference
import java.net.URL

internal class ContentPerformanceMonitorTest
{
    private val eventsReporter = mockk<EventsReporter>()
    private val keepAliveReporter = mockk<KeepAliveReporter>()
    private val previousKeepAliveDataSource = mockk<RingPublishingTrackingKeepAliveDataSource>()

    @Before
    fun setUp()
    {
        RingPublishingTracking.configurationManager.initializeConfiguration(
            RingPublishingTrackingConfiguration(
                tenantId = "tenant",
                apiKey = "api-key",
                applicationRootPath = "app",
            )
        )
        RingPublishingTracking.eventsReporter = eventsReporter
        RingPublishingTracking.keepAliveReporter = keepAliveReporter
        RingPublishingTracking.keepAliveDelegate = WeakReference(previousKeepAliveDataSource)
        setInitialized(true)
    }

    @After
    fun tearDown()
    {
        RingPublishingTracking.keepAliveDelegate = null
        setInitialized(false)
    }

    @Test
    fun reportContentPageView_WithoutKeepAlive_StopsPreviousTrackingAndReportsTypedEvent()
    {
        val reportedEvent = slot<com.ringpublishing.tracking.data.Event>()
        every { keepAliveReporter.stop() } just Runs
        every { eventsReporter.clientData(ContentViewType.TTS) } returns "encoded-client-data"
        every { eventsReporter.reportEvent(capture(reportedEvent)) } just Runs

        RingPublishingTracking.reportContentPageView(
            contentMetadata = ContentMetadata(
                publicationId = "publication-id",
                publicationUrl = URL("https://example.com/article"),
                sourceSystemName = "cms",
                paidContent = false,
                contentId = "content-id",
                contentSpaceUuid = "content-space-uuid",
            ),
            viewType = ContentViewType.TTS,
            contentPageViewSource = ContentPageViewSource.DEFAULT,
            currentStructurePath = listOf("article"),
            partiallyReloaded = false,
            contentKeepAliveDataSource = null,
        )

        verify(exactly = 1) { keepAliveReporter.stop() }
        Assert.assertNull(RingPublishingTracking.keepAliveDelegate)
        Assert.assertEquals(
            "encoded-client-data",
            reportedEvent.captured.parameters[EventParam.CLIENT_ID.text],
        )
    }

    private fun setInitialized(value: Boolean)
    {
        RingPublishingTracking::class.java.getDeclaredField("isInitialized").apply {
            isAccessible = true
            setBoolean(null, value)
        }
    }
}
