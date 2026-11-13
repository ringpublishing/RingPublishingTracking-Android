package com.ringpublishing.tracking.demo.controller

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.createMediaSession
import com.ringpublishing.tracking.data.media.MediaAudioOutput
import com.ringpublishing.tracking.data.media.MediaContentConfiguration
import com.ringpublishing.tracking.data.media.MediaContentSubtype
import com.ringpublishing.tracking.data.media.MediaContentType
import com.ringpublishing.tracking.data.media.MediaMonetizationType
import com.ringpublishing.tracking.data.media.MediaPauseReason
import com.ringpublishing.tracking.data.media.MediaPlaybackEndReason
import com.ringpublishing.tracking.data.media.MediaPlaybackIntent
import com.ringpublishing.tracking.data.media.MediaPlayingStartParameters
import com.ringpublishing.tracking.data.media.MediaStartMode
import com.ringpublishing.tracking.data.media.MediaType
import com.ringpublishing.tracking.data.media.MediaVisibilityState
import com.ringpublishing.tracking.demo.data.ScreenTrackingData

class MediaActionsController : ScreenController() {

    init {
        screenTrackingData = ScreenTrackingData(listOf("Home", "MediaActions"), "MediaActionsAdsArea")
    }

    /**
     * Audio session with two contents, a held resume during a seek, an output change and an error.
     */
    fun runAudioScenario() {
        val session = RingPublishingTracking.createMediaSession(MediaType.AUDIO, "main")

        val first = session.createMediaContent(
            MediaContentConfiguration(
                monetizationType = MediaMonetizationType.SUBSCRIPTION,
                visibilityState = MediaVisibilityState.BACKGROUND,
                muted = false,
                playbackRate = 1.5,
                audioOutput = MediaAudioOutput.BLUETOOTH
            )
        )
        first.playingStart(
            MediaPlayingStartParameters(
                mediaId = "a1",
                durationSec = 1800.0,
                mediaContentType = MediaContentType.MAIN,
                playbackIntent = MediaPlaybackIntent.USER_INITIATED,
                positionSec = 0.0,
                mediaVariant = "mp3",
                userEngaged = true
            )
        )
        first.playingProgress(15.0)
        first.pause(42.0, MediaPauseReason.USER_ACTION)
        first.playingProgress(43.0)
        first.seekStart(42.0, targetSec = 100.0)
        first.resume(42.0)
        first.seekEnd(100.0)
        first.outputChange(110.0, MediaAudioOutput.CAR)
        first.playingEnd(1800.0, MediaPlaybackEndReason.NATURAL_COMPLETION)
        first.pause(1800.0, MediaPauseReason.USER_ACTION)

        val second = session.createMediaContent(MediaContentConfiguration(monetizationType = MediaMonetizationType.FREE))
        second.playingStart(
            MediaPlayingStartParameters(
                mediaId = "a2",
                durationSec = 600.0,
                mediaContentType = MediaContentType.MAIN,
                playbackIntent = MediaPlaybackIntent.CONTINUATION,
                positionSec = 0.0
            )
        )
        second.visibilityChange(5.0, MediaVisibilityState.VISIBLE)
        second.error(6.0, "E42", "decoder failure")
        session.end()
        second.resume(7.0)
        session.end()

        val inert = session.createMediaContent()
        inert.playingStart(
            MediaPlayingStartParameters(
                mediaId = "a3",
                durationSec = 1.0,
                mediaContentType = MediaContentType.MAIN,
                playbackIntent = MediaPlaybackIntent.USER_INITIATED,
                positionSec = 0.0
            )
        )
    }

    /**
     * Video session with a main content interrupted by a mid-roll ad, a seek and a restart of the same content.
     */
    fun runVideoScenario() {
        val session = RingPublishingTracking.createMediaSession(MediaType.VIDEO, "inline")

        val main = session.createMediaContent(MediaContentConfiguration(monetizationType = MediaMonetizationType.FREE))
        main.playingStart(
            MediaPlayingStartParameters(
                mediaId = "v1",
                durationSec = 120.0,
                mediaContentType = MediaContentType.MAIN,
                playbackIntent = MediaPlaybackIntent.AUTOPLAY,
                positionSec = 0.0,
                startMode = MediaStartMode.MUTED_START
            )
        )
        main.unmute(3.0)

        val ad = session.createMediaContent()
        ad.playingStart(
            MediaPlayingStartParameters(
                mediaId = "ad1",
                durationSec = 10.0,
                mediaContentType = MediaContentType.AD,
                playbackIntent = MediaPlaybackIntent.AUTOPLAY,
                positionSec = 0.0,
                mediaContentSubtype = MediaContentSubtype.MIDROLL,
                adCampaignId = "camp1"
            )
        )
        ad.playingEnd(10.0, MediaPlaybackEndReason.NATURAL_COMPLETION)
        main.seekEnd(30.0)
        main.mute(31.0)
        main.playingEnd(120.0, MediaPlaybackEndReason.USER_STOPPED)
        main.playingStart(
            MediaPlayingStartParameters(
                mediaId = "v1",
                durationSec = 120.0,
                mediaContentType = MediaContentType.MAIN,
                playbackIntent = MediaPlaybackIntent.USER_INITIATED,
                positionSec = 0.0
            )
        )
        main.seekStart(50.0, targetSec = 80.0)
        main.seekStart(55.0)
        session.end()
    }
}
