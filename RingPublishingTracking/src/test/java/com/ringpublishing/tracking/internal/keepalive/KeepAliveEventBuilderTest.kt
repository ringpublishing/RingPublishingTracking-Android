/*
 *  Created by Grzegorz Małopolski on 10/27/21, 4:45 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentSize
import com.ringpublishing.tracking.data.KeepAliveContentStatus
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.decorator.EventParam
import com.ringpublishing.tracking.internal.factory.EventType
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class KeepAliveEventBuilderTest
{
    private val gson = GsonBuilder().create()

    @MockK
	lateinit var screenSizeInfo: ScreenSizeInfo

	@MockK
	lateinit var contentMetadata: ContentMetadata

	@MockK
	lateinit var keepAliveMetaData: KeepAliveMetadata

	@MockK
	lateinit var keepAliveContentStatus: KeepAliveContentStatus

	@MockK
	lateinit var keepAliveMeasureType: KeepAliveMeasureType

	@MockK
	lateinit var contentSize: ContentSize

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		RingPublishingTracking.setDebugMode(true)
	}

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

	@Test
	fun create_WhenOneMetaDataAdded_ThenEventHaveAllCorrectValues()
	{
		every { screenSizeInfo.getWindowSizeDpString(any()) } returns "100x100"
		every { screenSizeInfo.getSizeDp(300) } returns 300
		every { contentSize.widthPx } returns 100
		every { contentSize.heightPx } returns 100
		every { keepAliveContentStatus.contentSizePx } returns contentSize
		every { keepAliveContentStatus.scrollOffsetPx } returns 300
		every { keepAliveMetaData.contentStatus } returns keepAliveContentStatus

		every { keepAliveMeasureType.text } returns KeepAliveMeasureType.DOCUMENT_ALIVE.text
		every { keepAliveMetaData.measureType } returns keepAliveMeasureType

		every { keepAliveMetaData.timingInMillis } returns 5000L

		every { contentMetadata.paidContent } returns false
		every { contentMetadata.sourceSystemName } returns "sourceSystemName"
		every { contentMetadata.publicationId } returns "publicationId"
		every { contentMetadata.contentPartIndex } returns 50
		every { contentMetadata.contentId } returns "1"

        val builder = KeepAliveEventBuilder(screenSizeInfo, gson)

		val event = builder.create(contentMetadata, mutableListOf(keepAliveMetaData))

		Assert.assertEquals(AnalyticsSystem.TIMESCORE.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.KEEP_ALIVE.text, event.name)

		Assert.assertEquals("PV_4,sourceSystemName,publicationId,50,f", event.parameters["DX"])
		Assert.assertEquals(1, event.parameters["KTA"])
		Assert.assertEquals("100x100", (event.parameters["KDS"] as Array<*>)[0])
		Assert.assertEquals("A", (event.parameters["KMT"] as Array<*>)[0])
		Assert.assertEquals(1, (event.parameters["KHF"] as Array<*>)[0])
		Assert.assertEquals(5L, (event.parameters["KTP"] as Array<*>)[0])
        Assert.assertEquals(mockRdlcnEncodingNotPaid(), event.parameters[EventParam.MARKED_AS_PAID_DATA.text])
    }

    private fun mockRdlcnEncodingNotPaid() = encode(
        "{\"publication\":{\"premium\":false},\"source\":{\"id\":\"1\",\"system\":\"sourceSystemName\"}}"
    )

    private fun encode(input: String): String {
        return Base64.encodeToString(
            input.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
    }
}
