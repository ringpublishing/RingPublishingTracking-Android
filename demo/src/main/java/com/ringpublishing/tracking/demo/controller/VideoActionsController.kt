/*
 *  Created by Grzegorz Małopolski on 9/23/21, 1:51 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.controller

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.demo.data.ScreenTrackingData
import com.ringpublishing.tracking.demo.sample.sampleVideoMetadata
import com.ringpublishing.tracking.demo.sample.sampleVideoState
import com.ringpublishing.tracking.data.video.VideoEvent
import com.ringpublishing.tracking.reportVideoEvent

class VideoActionsController : ScreenController() {

    init {
        screenTrackingData = ScreenTrackingData(listOf("Home", "VideoActions"), "VideoActionsAdsArea")
    }

    fun actionVideoStart() {
        RingPublishingTracking.reportVideoEvent(
            videoEvent = VideoEvent.START,
            videoMetadata = sampleVideoMetadata,
            videoState = sampleVideoState
        )
    }

    fun actionVideoPlayingStart() {
        RingPublishingTracking.reportVideoEvent(
            videoEvent = VideoEvent.PLAYING_START,
            videoMetadata = sampleVideoMetadata,
            videoState = sampleVideoState
        )
    }

    fun actionVideoPlayingAutostart() {
        RingPublishingTracking.reportVideoEvent(
            videoEvent = VideoEvent.PLAYING_AUTOSTART,
            videoMetadata = sampleVideoMetadata,
            videoState = sampleVideoState
        )
    }

    fun actionVideoPaused() {
        RingPublishingTracking.reportVideoEvent(
            videoEvent = VideoEvent.PAUSED,
            videoMetadata = sampleVideoMetadata,
            videoState = sampleVideoState
        )
    }

    fun actionVideoPlaying() {
        RingPublishingTracking.reportVideoEvent(
            videoEvent = VideoEvent.PLAYING,
            videoMetadata = sampleVideoMetadata,
            videoState = sampleVideoState
        )
    }

    fun actionVideoKeepPlaying() {
        RingPublishingTracking.reportVideoEvent(
            videoEvent = VideoEvent.KEEP_PLAYING,
            videoMetadata = sampleVideoMetadata,
            videoState = sampleVideoState
        )
    }

    fun actionVideoPlayingEnd() {
        RingPublishingTracking.reportVideoEvent(
            videoEvent = VideoEvent.PLAYING_END,
            videoMetadata = sampleVideoMetadata,
            videoState = sampleVideoState
        )
    }
}
