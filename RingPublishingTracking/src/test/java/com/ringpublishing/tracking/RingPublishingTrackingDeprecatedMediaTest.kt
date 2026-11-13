@file:Suppress("DEPRECATION")

package com.ringpublishing.tracking

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.audio.AudioContentCategory
import com.ringpublishing.tracking.data.audio.AudioEvent
import com.ringpublishing.tracking.data.audio.AudioMediaType
import com.ringpublishing.tracking.data.audio.AudioMetadata
import com.ringpublishing.tracking.data.audio.AudioOutput
import com.ringpublishing.tracking.data.audio.AudioPlayerVisibilityState
import com.ringpublishing.tracking.data.audio.AudioState
import com.ringpublishing.tracking.data.audio.AudioStreamFormat
import com.ringpublishing.tracking.data.media.MediaType
import com.ringpublishing.tracking.data.video.VideoAdsConfiguration
import com.ringpublishing.tracking.data.video.VideoContentCategory
import com.ringpublishing.tracking.data.video.VideoEvent
import com.ringpublishing.tracking.data.video.VideoMetadata
import com.ringpublishing.tracking.data.video.VideoStartMode
import com.ringpublishing.tracking.data.video.VideoState
import com.ringpublishing.tracking.data.video.VideoStreamFormat
import com.ringpublishing.tracking.data.video.VideoVisibilityState
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.factory.AudioEventsFactory
import com.ringpublishing.tracking.internal.factory.VideoEventsFactory
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * The deprecated report methods keep sending the legacy VidEvent exactly as before.
 */
class RingPublishingTrackingDeprecatedMediaTest {

    private val gson = GsonBuilder().create()
    private val reported = mutableListOf<Event>()

    private val videoMetadata = VideoMetadata(
        publicationId = "2305009.546843861",
        contentId = "7f5d98d1-6a69-4f08-89b9-97d55e2372ba",
        isMainContentPiece = true,
        videoStreamFormat = VideoStreamFormat.HLS,
        videoDuration = 123,
        videoContentCategory = VideoContentCategory.FREE,
        videoAdsConfiguration = VideoAdsConfiguration.ENABLED,
        videoPlayerVersion = "1.0.2"
    )

    private val videoState = VideoState(
        currentTime = 1000,
        currentBitrate = "1800",
        isMuted = false,
        visibilityState = VideoVisibilityState.VISIBLE,
        startMode = VideoStartMode.NORMAL
    )

    private val audioMetadata = AudioMetadata(
        contentId = "66fb0c5a-f98b-49a2-bc20-ee6d9477ade5",
        contentTitle = "Title",
        contentSeriesId = "67",
        contentSeriesTitle = "Series",
        mediaType = AudioMediaType.PODCAST,
        audioDuration = 3722,
        audioStreamFormat = AudioStreamFormat.MP3,
        isContentFragment = false,
        audioContentCategory = AudioContentCategory.FREE,
        audioPlayerVersion = "1.0.0"
    )

    private val audioState = AudioState(
        currentTime = 1000,
        currentBitrate = 360,
        visibilityState = AudioPlayerVisibilityState.BACKGROUND,
        audioOutput = AudioOutput.BLUETOOTH
    )

    @Before
    fun before() {
        mockkStatic(Base64::class)
        val bytes = slot<ByteArray>()
        every { Base64.encodeToString(capture(bytes), Base64.NO_WRAP) } answers { java.util.Base64.getEncoder().encodeToString(bytes.captured) }
        mockkObject(RingPublishingTracking)
        every { RingPublishingTracking.reportEvent(capture(reported)) } returns Unit
    }

    @After
    fun after() {
        unmockkAll()
    }

    /** 'RR' is a random request number, the rest of the event is deterministic. */
    private fun assertSameAsFactory(expected: Event) {
        assertEquals(expected.analyticsSystemName, reported[0].analyticsSystemName)
        assertEquals(expected.name, reported[0].name)
        assertEquals(expected.parameters - "RR", reported[0].parameters - "RR")
    }

    @Test
    fun reportVideoEvent_StillSendsLegacyVidEvent() {
        RingPublishingTracking.reportVideoEvent(VideoEvent.START, videoMetadata, videoState)

        assertEquals(1, reported.size)
        assertEquals("VidEvent", reported[0].parameters["RT"])
        assertSameAsFactory(VideoEventsFactory(gson).createVideoEvent(VideoEvent.START, videoMetadata, videoState))
    }

    @Test
    fun reportAudioEvent_StillSendsLegacyVidEvent() {
        RingPublishingTracking.reportAudioEvent(AudioEvent.PLAYING_START, audioMetadata, audioState)

        assertEquals(1, reported.size)
        assertEquals("VidEvent", reported[0].parameters["RT"])
        assertSameAsFactory(AudioEventsFactory(gson).createAudioEvent(AudioEvent.PLAYING_START, audioMetadata, audioState))
    }

    @Test
    fun createMediaSession_SendsMediaEventThroughReportEvent() {
        val session = RingPublishingTracking.createMediaSession(MediaType.AUDIO, "main")

        session.createMediaContent()

        assertEquals(1, reported.size)
        assertEquals("MediaEvent", reported[0].name)
        assertEquals(AnalyticsSystem.GENERIC.text, reported[0].analyticsSystemName)
        assertEquals("main", reported[0].parameters["player_variant"])
    }
}
