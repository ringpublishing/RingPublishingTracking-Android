/*
 *  Created by Grzegorz Małopolski on 10/27/21, 4:45 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentSize
import com.ringpublishing.tracking.data.KeepAliveContentStatus
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.factory.EventType
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class KeepAliveEventBuilderTest
{

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

		every { contentMetadata.contentWasPaidFor } returns false
		every { contentMetadata.sourceSystemName } returns "sourceSystemName"
		every { contentMetadata.publicationId } returns "publicationId"
		every { contentMetadata.contentPartIndex } returns 50

		val builder = KeepAliveEventBuilder(screenSizeInfo)

		val event = builder.create(contentMetadata, mutableListOf(keepAliveMetaData))

		Assert.assertEquals(AnalyticsSystem.TIMESCORE.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.KEEP_ALIVE.text, event.name)

		Assert.assertEquals("PV_4,sourceSystemName,publicationId,50,f", event.parameters["DX"])
		Assert.assertEquals(1, event.parameters["KTA"])
		Assert.assertEquals("100x100", (event.parameters["KDS"] as Array<*>)[0])
		Assert.assertEquals("A", (event.parameters["KMT"] as Array<*>)[0])
		Assert.assertEquals(1, (event.parameters["KHF"] as Array<*>)[0])
		Assert.assertEquals(5L, (event.parameters["KTP"] as Array<*>)[0])
	}
}
