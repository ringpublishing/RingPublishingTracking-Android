/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentSize
import com.ringpublishing.tracking.data.KeepAliveContentStatus
import com.ringpublishing.tracking.data.effectivepageview.EffectivePageViewComponentSource
import com.ringpublishing.tracking.data.effectivepageview.EffectivePageViewMetadata
import com.ringpublishing.tracking.data.effectivepageview.EffectivePageViewTriggerSource
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.data.WindowSize
import com.ringpublishing.tracking.internal.decorator.EventParam
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewEventParam
import com.ringpublishing.tracking.internal.factory.EffectivePageViewEventFactory
import com.ringpublishing.tracking.internal.factory.EventType
import com.ringpublishing.tracking.internal.factory.UserEventParam
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import com.ringpublishing.tracking.internal.util.buildToDX
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL

internal class EffectivePageViewEventFactoryTest {

    private val gson = GsonBuilder().create()

    @MockK
    lateinit var screenSizeInfo: ScreenSizeInfo

    @MockK
    lateinit var configurationManager: ConfigurationManager

    private lateinit var sampleEventFactory: EffectivePageViewEventFactory

    private val ePVVersion = "1.1"

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
        componentSource = EffectivePageViewComponentSource.AUDIO,
        triggerSource = EffectivePageViewTriggerSource.PLAY,
        measurement = sampleContentStatus
    )

    @Before
    fun `Bypass android_util_Base64 to java_util_Base64`() {
        mockkStatic(Base64::class)
        val arraySlot = slot<ByteArray>()

        every {
            Base64.encodeToString(capture(arraySlot), Base64.NO_WRAP)
        } answers {
            java.util.Base64.getEncoder().encodeToString(arraySlot.captured)
        }
    }

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        every { screenSizeInfo.getScreenSizePxFromMetrics() } returns WindowSize(1000, 2000)
        every { configurationManager.shouldReportEffectivePageViewEvent() } returns true

        sampleEventFactory = EffectivePageViewEventFactory(screenSizeInfo, gson, configurationManager)
    }

    @Test
    fun whenSendingIsBlockedByConfiguration_thenShouldNotSendEvent() {

        // Overriding configuration to simulate that sending of effective page view events is blocked
        every { configurationManager.shouldReportEffectivePageViewEvent() } returns false

        // Assert that the event should not be sent
        Assert.assertFalse(sampleEventFactory.shouldSendEvent(sampleEffectivePageViewMetadata))
    }

    @Test
    fun whenScrollIsToLittle_thenShouldNotSentEvent() {

        // Overriding sampleEffectivePageViewMetadata to simulate a scroll event with insufficient scroll offset
        val sampleEffectivePageViewMetadata = sampleEffectivePageViewMetadata.copy(
            componentSource = EffectivePageViewComponentSource.SCROLL,
            triggerSource = EffectivePageViewTriggerSource.SCROLL,
        )

        // Assert that the event should not be sent - scroll value 0 is to little - it is not >= 2 * screen height
        Assert.assertFalse(sampleEventFactory.shouldSendEvent(sampleEffectivePageViewMetadata))
    }

    @Test
    fun whenScrollIsSufficient_thenShouldSendEvent() {

        // Overriding sampleContentStatus with scroll offset that is sufficient for sending the event (2* screen height)
        val sampleContentStatus = sampleContentStatus.copy(scrollOffsetPx = 4000)

        // Overriding sampleEffectivePageViewMetadata to simulate a scroll event with sufficient scroll offset
        val sampleEffectivePageViewMetadata = sampleEffectivePageViewMetadata.copy(
            componentSource = EffectivePageViewComponentSource.SCROLL,
            triggerSource = EffectivePageViewTriggerSource.SCROLL,
            measurement = sampleContentStatus
        )

        // Assert that the event should not be sent - scroll value 4000 is enough - it is matching the >= 2 * screen height constraint
        Assert.assertTrue(sampleEventFactory.shouldSendEvent(sampleEffectivePageViewMetadata))
    }

    @Test
    fun createEffectivePageViewEvent_thenShouldNotSendEventAgain_thenShouldSentAfterReset() {

        // Assert that the event should be sent - this is the first call
        Assert.assertTrue(sampleEventFactory.shouldSendEvent(sampleEffectivePageViewMetadata))

        // Create an event, now is event sent should be true and should not send again
        sampleEventFactory.create(sampleContentMetadata, sampleEffectivePageViewMetadata)

        // Assert that the event should not be sent again - isEventSent is now true
        Assert.assertFalse(sampleEventFactory.shouldSendEvent(sampleEffectivePageViewMetadata))

        sampleEventFactory.reset()

        // Assert that the event should be sent - this is the first call after reset
        Assert.assertTrue(sampleEventFactory.shouldSendEvent(sampleEffectivePageViewMetadata))
    }

    @Test
    fun createEffectivePageViewEvent_thenParameterValues() {

        // Create an event with sample metadata
        val event = sampleEventFactory.create(sampleContentMetadata, sampleEffectivePageViewMetadata)

        // Assert that the event is not null and its parameters have expected values
        Assert.assertEquals(event.parameters[EffectivePageViewEventParam.VERSION.text], ePVVersion)
        Assert.assertEquals(event.parameters[EffectivePageViewEventParam.COMPONENT_SOURCE.text], sampleEffectivePageViewMetadata.componentSource.text)
        Assert.assertEquals(event.parameters[EffectivePageViewEventParam.TRIGGER_SOURCE.text], sampleEffectivePageViewMetadata.triggerSource.text)
        Assert.assertEquals(event.parameters[EffectivePageViewEventParam.SCROLL_HEIGHT.text], sampleEffectivePageViewMetadata.measurement.contentSizePx.heightPx)
        Assert.assertEquals(event.parameters[EffectivePageViewEventParam.SCROLL_TOP.text], sampleEffectivePageViewMetadata.measurement.scrollOffsetPx)
        Assert.assertEquals(event.parameters[EffectivePageViewEventParam.VIEWPORT_HEIGHT.text], screenSizeInfo.getScreenSizePxFromMetrics().height)
        Assert.assertEquals(event.parameters[UserEventParam.PAGE_VIEW_CONTENT_INFO.text], sampleContentMetadata.buildToDX())
        Assert.assertEquals(event.parameters[UserEventParam.PAGE_VIEW_RESOURCE_IDENTIFIER.text], sampleContentMetadata.contentId)
        Assert.assertEquals(event.parameters[EventParam.MARKED_AS_PAID_DATA.text], mockRDLCNEncodingPaid())
        Assert.assertEquals(event.analyticsSystemName, AnalyticsSystem.KROPKA_STATS.text)
        Assert.assertEquals(event.name, EventType.POLARIS.text)
    }

    /**
     * Mocks RDLCN param value
     */
    private fun mockRDLCNEncodingPaid() = encode(
        "{\"publication\":{\"premium\":${sampleContentMetadata.paidContent}},\"source\":{\"id\":\"${sampleContentMetadata.contentId}\"" +
                ",\"system\":\"${sampleContentMetadata.sourceSystemName}\"}}"
    )

    /**
     * Encoding helper function
     */
    private fun encode(input: String): String {
        return Base64.encodeToString(
            input.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
    }
}
