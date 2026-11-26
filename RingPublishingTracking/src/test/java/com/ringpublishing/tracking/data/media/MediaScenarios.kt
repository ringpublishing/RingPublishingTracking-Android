package com.ringpublishing.tracking.data.media

/**
 * Call sequences of the MediaEvent parity scenarios.
 */
internal object MediaScenarios {

    fun audio(session: MediaSession) {
        val c1 = session.createMediaContent(
            MediaContentConfiguration(
                monetizationType = MediaMonetizationType.SUBSCRIPTION,
                visibilityState = MediaVisibilityState.BACKGROUND,
                muted = false,
                playbackRate = 1.5,
                audioOutput = MediaAudioOutput.BLUETOOTH
            )
        )
        c1.playingStart(
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
        c1.playingProgress(15.0)
        c1.pause(42.0, MediaPauseReason.USER_ACTION)
        c1.playingProgress(43.0)
        c1.seekStart(42.0, targetSec = 100.0)
        c1.resume(42.0)
        c1.seekEnd(100.0)
        c1.outputChange(110.0, MediaAudioOutput.CAR)
        c1.playingEnd(1800.0, MediaPlaybackEndReason.NATURAL_COMPLETION)
        c1.pause(1800.0, MediaPauseReason.USER_ACTION)

        val c2 = session.createMediaContent(MediaContentConfiguration(monetizationType = MediaMonetizationType.FREE))
        c2.playingStart(
            MediaPlayingStartParameters(
                mediaId = "a2",
                durationSec = 600.0,
                mediaContentType = MediaContentType.MAIN,
                playbackIntent = MediaPlaybackIntent.CONTINUATION,
                positionSec = 0.0
            )
        )
        c2.visibilityChange(5.0, MediaVisibilityState.VISIBLE)
        c2.error(6.0, "E42", "decoder failure")
        session.end()
        c2.resume(7.0)
        session.end()

        val c3 = session.createMediaContent()
        c3.playingStart(
            MediaPlayingStartParameters(
                mediaId = "a3",
                durationSec = 1.0,
                mediaContentType = MediaContentType.MAIN,
                playbackIntent = MediaPlaybackIntent.USER_INITIATED,
                positionSec = 0.0
            )
        )
    }

    fun video(session: MediaSession) {
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

    fun ttsEmpty(session: MediaSession) {
        session.end()
    }
}
