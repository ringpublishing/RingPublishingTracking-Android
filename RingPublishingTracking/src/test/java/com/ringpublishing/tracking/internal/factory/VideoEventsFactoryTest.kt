/*
 *  Created by Grzegorz Małopolski on 10/11/21, 2:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.internal.factory.EventType
import com.ringpublishing.tracking.internal.video.VideoEventParam
import com.ringpublishing.tracking.internal.factory.VideoEventsFactory
import com.ringpublishing.tracking.data.video.VideoAdsConfiguration
import com.ringpublishing.tracking.data.video.VideoContentCategory
import com.ringpublishing.tracking.data.video.VideoEvent
import com.ringpublishing.tracking.data.video.VideoMetadata
import com.ringpublishing.tracking.data.video.VideoStartMode
import com.ringpublishing.tracking.data.video.VideoState
import com.ringpublishing.tracking.data.video.VideoStreamFormat
import com.ringpublishing.tracking.data.video.VideoVisibilityState
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VideoEventsFactoryTest {

    private val gson = GsonBuilder().create()

    private val sampleVideoMetadata = VideoMetadata(
        publicationId = "2305009.546843861",
        contentId = "7f5d98d1-6a69-4f08-89b9-97d55e2372ba",
        isMainContentPiece = true,
        videoStreamFormat = VideoStreamFormat.HLS,
        videoDuration = 123,
        videoContentCategory = VideoContentCategory.FREE,
        videoAdsConfiguration = VideoAdsConfiguration.ENABLED,
        videoPlayerVersion = "1.0.2"
    )

    private val sampleVideoState = VideoState(
        currentTime = (System.currentTimeMillis() / 1000).toInt(),
        currentBitrate = "1800",
        isMuted = false,
        visibilityState = VideoVisibilityState.VISIBLE,
        startMode = VideoStartMode.NORMAL
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

    @Test
    fun createVideoEvent_StringParameters_ThenParametersInEvent() {
        val eventsFactory = VideoEventsFactory(gson)
        val event = eventsFactory.createVideoEvent(VideoEvent.START, sampleVideoMetadata, sampleVideoState)

        Assert.assertTrue(event.parameters.isNotEmpty())
    }

    @Test
    fun createVideoEvent_WhenAdsParameterIsNull_ThenAdsParameterNotInEvent() {
        val eventsFactory = VideoEventsFactory(gson)
        val event = eventsFactory.createVideoEvent(VideoEvent.START, sampleVideoMetadata, sampleVideoState)

        Assert.assertFalse(event.parameters.containsKey(VideoEventParam.ADS_CONFIGURATION.text))
    }

    @Test
    fun createVideoEvent_WhenAdsParameterIsNotNull_ThenAdsParameterInEvent() {
        val eventsFactory = VideoEventsFactory(gson)
        val sampleVideoMetadata = sampleVideoMetadata.copy(videoAdsConfiguration = VideoAdsConfiguration.DISABLED_BY_CONFIGURATION)
        val event = eventsFactory.createVideoEvent(VideoEvent.START, sampleVideoMetadata, sampleVideoState)

        Assert.assertTrue(event.parameters.containsKey(VideoEventParam.ADS_CONFIGURATION.text))
    }

    @Test
    fun createMultipleVideoEvents_WhenOneContentId_ThenCorrectEventCounter() {
        val eventsFactory = VideoEventsFactory(gson)

        val event0 = eventsFactory.createVideoEvent(VideoEvent.PLAYING, sampleVideoMetadata, sampleVideoState)
        val event1 = eventsFactory.createVideoEvent(VideoEvent.PLAYING, sampleVideoMetadata, sampleVideoState)
        val event2 = eventsFactory.createVideoEvent(VideoEvent.PLAYING, sampleVideoMetadata, sampleVideoState)
        val event3 = eventsFactory.createVideoEvent(VideoEvent.START, sampleVideoMetadata, sampleVideoState)

        Assert.assertEquals(event0.parameters[VideoEventParam.COUNTER.text], 0)
        Assert.assertEquals(event1.parameters[VideoEventParam.COUNTER.text], 1)
        Assert.assertEquals(event2.parameters[VideoEventParam.COUNTER.text], 2)
        Assert.assertEquals(event3.parameters[VideoEventParam.COUNTER.text], 0)
    }

    @Test
    fun createMultipleVideoEvents_WhenMultipleContentIds_ThenCorrectEventCounter() {
        val eventsFactory = VideoEventsFactory(gson)

        val sampleVideoMetadata0 = sampleVideoMetadata.copy(contentId = "0")
        val sampleVideoMetadata1 = sampleVideoMetadata.copy(contentId = "1")

        val event00 = eventsFactory.createVideoEvent(VideoEvent.START, sampleVideoMetadata0, sampleVideoState)
        val event01 = eventsFactory.createVideoEvent(VideoEvent.PLAYING, sampleVideoMetadata0, sampleVideoState)
        val event02 = eventsFactory.createVideoEvent(VideoEvent.PLAYING, sampleVideoMetadata0, sampleVideoState)

        val event10 = eventsFactory.createVideoEvent(VideoEvent.START, sampleVideoMetadata1, sampleVideoState)
        val event11 = eventsFactory.createVideoEvent(VideoEvent.PLAYING, sampleVideoMetadata1, sampleVideoState)
        val event12 = eventsFactory.createVideoEvent(VideoEvent.START, sampleVideoMetadata1, sampleVideoState)

        Assert.assertEquals(event00.parameters[VideoEventParam.COUNTER.text], 0)
        Assert.assertEquals(event01.parameters[VideoEventParam.COUNTER.text], 1)
        Assert.assertEquals(event02.parameters[VideoEventParam.COUNTER.text], 2)
        Assert.assertEquals(event10.parameters[VideoEventParam.COUNTER.text], 0)
        Assert.assertEquals(event11.parameters[VideoEventParam.COUNTER.text], 1)
        Assert.assertEquals(event12.parameters[VideoEventParam.COUNTER.text], 0)
    }

    @Test
    fun createVideoEvent_StringParameters_ThenParameterValues() {
        val eventsFactory = VideoEventsFactory(gson)
        val videoEvent = VideoEvent.PLAYING
        val paramData = eventsFactory.createVideoEventVCParameter(sampleVideoMetadata, sampleVideoState)
        val paramTimestamp = eventsFactory.getAndUpdateSessionTimestamp(sampleVideoMetadata.contentId, videoEvent)
        val paramVisibility = eventsFactory.createVisibilityParam(sampleVideoState.visibilityState)
        val event = eventsFactory.createVideoEvent(videoEvent, sampleVideoMetadata, sampleVideoState)

        Assert.assertEquals(event.parameters[VideoEventParam.SELECTED_ELEMENT_NAME.text], videoEvent.text)
        Assert.assertEquals(event.parameters[VideoEventParam.EVENT_TYPE.text], EventType.VIDEO.text)
        Assert.assertEquals(event.parameters[VideoEventParam.CONTENT_ID.text], sampleVideoMetadata.contentId)
        Assert.assertEquals(event.parameters[VideoEventParam.IS_MUTED.text], 0)
        Assert.assertEquals(event.parameters[VideoEventParam.LENGTH.text], sampleVideoMetadata.videoDuration)
        Assert.assertEquals(event.parameters[VideoEventParam.CURRENT_TIME.text], sampleVideoState.currentTime)
        Assert.assertEquals(event.parameters[VideoEventParam.DATA.text], paramData)
        Assert.assertEquals(event.parameters[VideoEventParam.TIMESTAMP.text], paramTimestamp)
        Assert.assertEquals(event.parameters[VideoEventParam.COUNTER.text], 0)
        Assert.assertEquals(event.parameters[VideoEventParam.FORMAT.text], sampleVideoMetadata.videoStreamFormat.text)
        Assert.assertEquals(event.parameters[VideoEventParam.START_MODE.text], sampleVideoState.startMode.text)
        Assert.assertEquals(event.parameters[VideoEventParam.PLAYER_VERSION.text], sampleVideoMetadata.videoPlayerVersion)
        Assert.assertEquals(event.parameters[VideoEventParam.IS_MAIN_CONTENT_PIECE.text], "player")
        Assert.assertEquals(event.parameters[VideoEventParam.IS_MAIN_VIDEO.text], "mainVideo")
        Assert.assertEquals(event.parameters[VideoEventParam.CONTENT_CATEGORY.text], sampleVideoMetadata.videoContentCategory.text)
        Assert.assertEquals(event.parameters[VideoEventParam.VISIBILITY.text], paramVisibility)
        Assert.assertEquals(event.parameters[VideoEventParam.ADS_CONFIGURATION.text], sampleVideoMetadata.videoAdsConfiguration.text)
    }
}
