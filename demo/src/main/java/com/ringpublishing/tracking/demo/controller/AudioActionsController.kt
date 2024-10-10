/*
 * Created by Daniel Całka on 10/01/24, 1:51 PM
 * Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.controller

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.audio.AudioEvent
import com.ringpublishing.tracking.demo.data.ScreenTrackingData
import com.ringpublishing.tracking.demo.sample.sampleAudioMetadata
import com.ringpublishing.tracking.demo.sample.sampleAudioState
import com.ringpublishing.tracking.reportAudioEvent

class AudioActionsController : ScreenController() {

    init {
        screenTrackingData = ScreenTrackingData(listOf("Home", "AudioActions"), "AudioActionsAdsArea")
    }

    fun actionAudioStart() {
        RingPublishingTracking.reportAudioEvent(
            audioEvent = AudioEvent.START,
            audioMetadata = sampleAudioMetadata,
            audioState = sampleAudioState
        )
    }

    fun actionAudioPlayingStart() {
        RingPublishingTracking.reportAudioEvent(
            audioEvent = AudioEvent.PLAYING_START,
            audioMetadata = sampleAudioMetadata,
            audioState = sampleAudioState
        )
    }

    fun actionAudioPlayingAutostart() {
        RingPublishingTracking.reportAudioEvent(
            audioEvent = AudioEvent.PLAYING_AUTOSTART,
            audioMetadata = sampleAudioMetadata,
            audioState = sampleAudioState
        )
    }

    fun actionAudioPaused() {
        RingPublishingTracking.reportAudioEvent(
            audioEvent = AudioEvent.PAUSED,
            audioMetadata = sampleAudioMetadata,
            audioState = sampleAudioState
        )
    }

    fun actionAudioPlaying() {
        RingPublishingTracking.reportAudioEvent(
            audioEvent = AudioEvent.PLAYING,
            audioMetadata = sampleAudioMetadata,
            audioState = sampleAudioState
        )
    }

    fun actionAudioKeepPlaying() {
        RingPublishingTracking.reportAudioEvent(
            audioEvent = AudioEvent.KEEP_PLAYING,
            audioMetadata = sampleAudioMetadata,
            audioState = sampleAudioState
        )
    }

    fun actionAudioPlayingEnd() {
        RingPublishingTracking.reportAudioEvent(
            audioEvent = AudioEvent.PLAYING_END,
            audioMetadata = sampleAudioMetadata,
            audioState = sampleAudioState
        )
    }
}
