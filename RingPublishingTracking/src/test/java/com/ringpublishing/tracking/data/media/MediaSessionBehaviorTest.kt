package com.ringpublishing.tracking.data.media

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.listener.LogListener
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MediaSessionBehaviorTest {

    private val events = mutableListOf<Event>()
    private val warnings = mutableListOf<String>()
    private val logListener = object : LogListener {
        override fun debug(message: String) = Unit
        override fun info(message: String) = Unit
        override fun warn(message: String) {
            synchronized(warnings) { warnings.add(message) }
        }
        override fun error(message: String) = Unit
    }

    @Before
    fun before() {
        Logger.addLogListener(logListener)
    }

    @After
    fun after() {
        Logger.removeLogListener(logListener)
    }

    private fun session(mediaType: MediaType = MediaType.VIDEO, playerVariant: String? = null) =
        MediaSession(mediaType, playerVariant, { synchronized(events) { events.add(it) } })

    private fun start(
        mediaId: String = "m1",
        type: MediaContentType = MediaContentType.MAIN,
        position: Double = 0.0,
        subtype: MediaContentSubtype? = null,
        relatedMediaId: String? = null
    ) = MediaPlayingStartParameters(
        mediaId = mediaId,
        durationSec = 100.0,
        mediaContentType = type,
        playbackIntent = MediaPlaybackIntent.AUTOPLAY,
        positionSec = position,
        mediaContentSubtype = subtype,
        relatedMediaId = relatedMediaId
    )

    private fun types() = events.map { it.parameters["event_type"] }

    @Test
    fun events_AreGenericMediaEventsWithRtAndNoNullValues() {
        val session = session(MediaType.TTS)
        session.createMediaContent().playingStart(start())

        events.forEach {
            assertEquals(AnalyticsSystem.GENERIC.text, it.analyticsSystemName)
            assertEquals("MediaEvent", it.name)
            assertEquals("MediaEvent", it.parameters["RT"])
            assertFalse(it.parameters.containsKey("player_variant"))
            assertFalse(it.parameters.containsKey("start_mode"))
        }
    }

    @Test
    fun updatePlaybackRate_ChangesLaterEventsAndSendsNothing() {
        val session = session()
        val content = session.createMediaContent()
        content.playingStart(start())
        assertEquals(1.0, events.last().parameters["playback_rate"])
        val countBefore = events.size

        session.updatePlaybackRate(2.0)

        assertEquals(countBefore, events.size)
        assertEquals(2.0, session.playbackRate, 0.0)
        content.playingProgress(5.0)
        assertEquals(2.0, events.last().parameters["playback_rate"])
    }

    @Test
    fun updatePlaybackRate_OnEndedSession_IsIgnored() {
        val session = session()
        session.createMediaContent()
        session.end()

        session.updatePlaybackRate(3.0)

        assertEquals(1.0, session.playbackRate, 0.0)
    }

    @Test
    fun validation_OversizedValues_WarnButStillSend() {
        val session = session(playerVariant = "v".repeat(65))
        val content = session.createMediaContent(MediaContentConfiguration(playbackRate = 20.0))

        content.playingStart(start(mediaId = "x".repeat(37), position = -1.0))
        content.error(1.0, "", "m".repeat(1025))

        assertEquals(listOf("MEDIA_SESSION_START", "MEDIA_PLAYING_START", "MEDIA_ERROR"), types())
        assertEquals(20.0, events[1].parameters["playback_rate"])
        assertEquals(-1.0, events[1].parameters["position_sec"])
        val text = warnings.joinToString("\n")
        listOf("playerVariant", "playbackRate", "mediaId", "positionSec", "'code'", "'message'").forEach {
            assertTrue("warning for $it in:\n$text", text.contains(it))
        }
    }

    @Test
    fun validation_AdWithoutSubtypeAndRelatedMedia_WarnsAndStillSends() {
        val content = session().createMediaContent()

        content.playingStart(start(mediaId = "ad", type = MediaContentType.AD))

        assertEquals("MEDIA_PLAYING_START", events.last().parameters["event_type"])
        assertTrue(warnings.any { it.contains("mediaContentSubtype") })
        assertTrue(warnings.any { it.contains("relatedMediaId") })
        assertFalse(events.last().parameters.containsKey("related_media_id"))
    }

    @Test
    fun ad_PausesPlayingMainAndResumesItAtContentPositionWhenEnded() {
        val session = session()
        val main = session.createMediaContent()
        main.playingStart(start(mediaId = "main"))
        main.playingProgress(40.0)
        val ad = session.createMediaContent()

        ad.playingStart(start(mediaId = "ad", type = MediaContentType.AD, subtype = MediaContentSubtype.PREROLL))
        ad.playingEnd(5.0, MediaPlaybackEndReason.NATURAL_COMPLETION)

        assertEquals(
            listOf("MEDIA_SESSION_START", "MEDIA_PLAYING_START", "MEDIA_PLAYING_PROGRESS", "MEDIA_PAUSE", "MEDIA_PLAYING_START", "MEDIA_PLAYING_END", "MEDIA_RESUME"),
            types()
        )
        val pause = events[3].parameters
        assertEquals("ad", pause["pause_reason"])
        assertEquals(40.0, pause["position_sec"])
        assertEquals(false, pause["user_engaged"])
        assertEquals("main", events[4].parameters["related_media_id"])
        assertEquals(40.0, events[6].parameters["position_sec"])
        assertEquals("main", events[6].parameters["media_id"])
    }

    @Test
    fun resume_DuringSeek_IsHeldUntilSeekEndAndDroppedByPause() {
        val session = session()
        val content = session.createMediaContent()
        content.playingStart(start())
        content.pause(10.0, MediaPauseReason.USER_ACTION)
        content.seekStart(10.0)
        content.resume(10.0)
        assertTrue(content.isSeeking)
        assertEquals(listOf("MEDIA_SESSION_START", "MEDIA_PLAYING_START", "MEDIA_PAUSE", "MEDIA_SEEK_START"), types())

        content.pause(11.0, MediaPauseReason.USER_ACTION)
        content.seekEnd(50.0)

        assertEquals(listOf("MEDIA_SESSION_START", "MEDIA_PLAYING_START", "MEDIA_PAUSE", "MEDIA_SEEK_START", "MEDIA_SEEK_END"), types())
        assertFalse(content.isSeeking)
    }

    @Test
    fun playingEnd_DuringSeek_ClosesSeekAtTargetFirst() {
        val session = session()
        val content = session.createMediaContent()
        content.playingStart(start())
        content.seekStart(10.0, targetSec = 70.0)

        content.playingEnd(70.0, MediaPlaybackEndReason.USER_STOPPED)

        assertEquals("MEDIA_SEEK_END", types()[3])
        assertEquals(70.0, events[3].parameters["position_sec"])
        assertEquals("MEDIA_PLAYING_END", types()[4])
    }

    @Test
    fun sessionEnd_DuringSeek_ClosesSeekThenEndsAndMakesContentInert() {
        val session = session()
        val content = session.createMediaContent()
        content.playingStart(start())
        content.seekStart(10.0, targetSec = 70.0)

        session.end()

        assertEquals(listOf("MEDIA_SESSION_START", "MEDIA_PLAYING_START", "MEDIA_SEEK_START", "MEDIA_SEEK_END", "MEDIA_SESSION_END"), types())
        assertEquals(70.0, events[3].parameters["position_sec"])
        assertEquals("m1", events.last().parameters["media_id"])
        assertFalse(session.isActive)
        assertFalse(content.hasActiveContent)
        content.playingProgress(1.0)
        assertEquals(5, events.size)
    }

    @Test
    fun mute_SetsUserEngagedFalseAndUnmuteTrueUnlessPassed() {
        val content = session().createMediaContent()
        content.playingStart(start())

        content.unmute(1.0)
        assertEquals(true, events.last().parameters["user_engaged"])
        content.mute(2.0)
        assertEquals(false, events.last().parameters["user_engaged"])
        content.mute(3.0, userEngaged = true)
        assertEquals(true, events.last().parameters["user_engaged"])
        assertEquals(true, events.last().parameters["muted"])
    }

    @Test
    fun sessionStart_IsSentOnceAndOnlyBeforeEnd() {
        val session = session()
        session.createMediaContent(MediaContentConfiguration(monetizationType = MediaMonetizationType.AD))
        session.createMediaContent()
        assertEquals(1, events.size)
        assertEquals(1, session.eventOrdinal)
        session.end()
        session.createMediaContent()
        assertEquals(listOf("MEDIA_SESSION_START", "MEDIA_SESSION_END"), types())
    }

    @Test
    fun sessionEnd_WithoutAnyContent_SendsNothing() {
        val session = session()
        session.end()
        assertTrue(events.isEmpty())
        assertEquals(0, session.eventOrdinal)
        assertNull(events.firstOrNull())
    }

    @Test
    fun concurrentCalls_GiveUniqueGapFreeOrdinalsInArrivalOrder() {
        val session = session(MediaType.AUDIO)
        val content = session.createMediaContent()
        content.playingStart(start())
        val threads = 8
        val callsPerThread = 250
        val pool = Executors.newFixedThreadPool(threads)
        val ready = CountDownLatch(threads)
        val go = CountDownLatch(1)

        repeat(threads) { t ->
            pool.execute {
                ready.countDown()
                go.await()
                repeat(callsPerThread) { i -> content.playingProgress((t * callsPerThread + i).toDouble()) }
            }
        }
        ready.await()
        go.countDown()
        pool.shutdown()
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS))

        val total = 2 + threads * callsPerThread
        assertEquals(total, events.size)
        assertEquals((1..total).toList(), events.map { it.parameters["event_ordinal"] })
        assertEquals(total, session.eventOrdinal)
    }

    @Test
    fun enums_UseContractRawValues() {
        assertEquals(listOf("video", "audio", "tts"), MediaType.values().map { it.text })
        assertEquals(listOf("visible", "hidden", "background", "pip"), MediaVisibilityState.values().map { it.text })
        assertEquals(listOf("speaker", "headphones", "bluetooth", "car", "chromecast", "external"), MediaAudioOutput.values().map { it.text })
        assertEquals(listOf("ad", "main", "teaser", "outro", "bumper"), MediaContentType.values().map { it.text })
        assertEquals(listOf("preroll", "midroll", "postroll"), MediaContentSubtype.values().map { it.text })
        assertEquals(listOf("free", "ad", "transactional", "subscription"), MediaMonetizationType.values().map { it.text })
        assertEquals(listOf("free", "basic", "premium", "vip"), MediaSubscriptionTier.values().map { it.text })
        assertEquals(listOf("free", "basic", "premium", "exclusive"), MediaTier.values().map { it.text })
        assertEquals(listOf("autoplay", "user_initiated", "continuation"), MediaPlaybackIntent.values().map { it.text })
        assertEquals(listOf("muted_start", "normal_start"), MediaStartMode.values().map { it.text })
        assertEquals(listOf("user_action", "network", "programmatic", "ad"), MediaPauseReason.values().map { it.text })
        assertEquals(listOf("natural_completion", "user_stopped", "error", "skipped"), MediaPlaybackEndReason.values().map { it.text })
    }
}
