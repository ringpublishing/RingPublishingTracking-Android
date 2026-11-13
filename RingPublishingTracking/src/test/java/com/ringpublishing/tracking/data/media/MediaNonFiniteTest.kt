package com.ringpublishing.tracking.data.media

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.queue.EventSizeCalculator
import com.ringpublishing.tracking.listener.LogListener
import com.ringpublishing.tracking.internal.di.Component
import com.ringpublishing.tracking.internal.di.provideGson
import io.mockk.mockk
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MediaNonFiniteTest {

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
        unmockkObject(Logger)
        Logger.addLogListener(logListener)
    }

    @After
    fun after() {
        Logger.removeLogListener(logListener)
    }

    private var lastSession: MediaSession? = null

    private fun session() = MediaSession(MediaType.VIDEO, null, { events.add(it) }).also { lastSession = it }

    private fun start(position: Double = 0.0, duration: Double = 100.0) = MediaPlayingStartParameters(
        mediaId = "m1",
        durationSec = duration,
        mediaContentType = MediaContentType.MAIN,
        playbackIntent = MediaPlaybackIntent.AUTOPLAY,
        positionSec = position
    )

    private fun playing(): MediaContent = session().createMediaContent().also { it.playingStart(start()) }

    private fun assertNoNonFinite() {
        events.forEach { event ->
            event.parameters.values.forEach {
                assertFalse("non-finite in ${event.parameters}", it is Double && !it.isFinite())
            }
        }
    }

    private fun assertWarned(method: String, name: String, value: Double) =
        assertTrue(
            warnings.toString(),
            warnings.any { it.contains("[MediaEvent] $method: '$name' must be a finite number, got $value") }
        )

    private fun assertSingleWarning() = assertEquals(warnings.toString(), 1, warnings.size)

    @Test
    fun positionSec_NaN_SendsEventWithoutPositionAndKeepsLastPosition() {
        val content = playing()
        content.playingProgress(12.0)

        content.playingProgress(Double.NaN)

        assertEquals("MEDIA_PLAYING_PROGRESS", events.last().parameters["event_type"])
        assertFalse(events.last().parameters.containsKey("position_sec"))
        assertWarned("playingProgress", "positionSec", Double.NaN)
        assertSingleWarning()
        assertNoNonFinite()

        // The last position stays 12.0, an ad pausing the content reports it.
        val adStart = events.size
        lastSession!!.createMediaContent().playingStart(
            MediaPlayingStartParameters(
                "ad1", 5.0, MediaContentType.AD, MediaPlaybackIntent.AUTOPLAY, 0.0,
                mediaContentSubtype = MediaContentSubtype.MIDROLL, relatedMediaId = "m1"
            )
        )
        assertEquals("MEDIA_PAUSE", events[adStart].parameters["event_type"])
        assertEquals(12.0, events[adStart].parameters["position_sec"])
    }

    @Test
    fun positionSec_Infinite_InEveryMethod_IsLeftOut() {
        val content = playing()
        val calls = listOf<Pair<String, () -> Unit>>(
            "playingProgress" to { content.playingProgress(Double.POSITIVE_INFINITY) },
            "seekStart" to { content.seekStart(Double.NEGATIVE_INFINITY) },
            "seekEnd" to { content.seekEnd(Double.NaN) },
            "mute" to { content.mute(Double.NaN) },
            "unmute" to { content.unmute(Double.NaN) },
            "outputChange" to { content.outputChange(Double.NaN, MediaAudioOutput.CAR) },
            "visibilityChange" to { content.visibilityChange(Double.NaN, MediaVisibilityState.PIP) },
            "error" to { content.error(Double.NaN, "E1", "boom") },
            "pause" to { content.pause(Double.NaN, MediaPauseReason.USER_ACTION) },
            "resume" to { content.resume(Double.NaN) },
            "playingEnd" to { content.playingEnd(Double.NaN, MediaPlaybackEndReason.USER_STOPPED) }
        )

        calls.forEach { (method, call) ->
            val before = events.size
            warnings.clear()
            call()
            assertEquals("$method sends one event", before + 1, events.size)
            assertFalse("$method leaves position out", events.last().parameters.containsKey("position_sec"))
            assertEquals("$method warns once: $warnings", 1, warnings.size)
            assertTrue(warnings[0].contains("$method: 'positionSec'"))
        }
        assertNoNonFinite()
    }

    @Test
    fun positionSec_NaN_InPlayingStart_IsLeftOut() {
        val content = session().createMediaContent()

        content.playingStart(start(position = Double.NaN))

        assertEquals("MEDIA_PLAYING_START", events.last().parameters["event_type"])
        assertFalse(events.last().parameters.containsKey("position_sec"))
        assertWarned("playingStart", "positionSec", Double.NaN)
        assertSingleWarning()
        assertNoNonFinite()
    }

    @Test
    fun durationSec_NaN_DropsDurationFromThisAndLaterEvents() {
        val content = session().createMediaContent()

        content.playingStart(start(duration = Double.NaN))
        content.playingProgress(1.0)

        val sent = events.filter { it.parameters["event_type"] != "MEDIA_SESSION_START" }
        assertEquals(2, sent.size)
        sent.forEach { assertFalse(it.parameters.containsKey("duration_sec")) }
        assertWarned("playingStart", "durationSec", Double.NaN)
        assertSingleWarning()
        assertNoNonFinite()
    }

    @Test
    fun targetSec_Infinite_IsTreatedAsNotGiven() {
        val content = playing()
        content.playingProgress(7.0)

        content.seekStart(8.0, targetSec = Double.POSITIVE_INFINITY)
        content.playingEnd(9.0, MediaPlaybackEndReason.USER_STOPPED)

        // Without a target, the seek closed by playingEnd reports the last position, 8.0 from seekStart.
        val seekEnd = events.first { it.parameters["event_type"] == "MEDIA_SEEK_END" }
        assertEquals(8.0, seekEnd.parameters["position_sec"])
        assertTrue(warnings.any { it.contains("seekStart: 'targetSec' must be a finite number, got Infinity") })
        assertEquals(warnings.toString(), 1, warnings.size)
        assertNoNonFinite()
    }

    @Test
    fun playbackRate_NaN_InConfigurationAndUpdate_KeepsSessionRate() {
        val session = session()
        val content = session.createMediaContent(MediaContentConfiguration(playbackRate = Double.NaN))
        assertEquals(1.0, session.playbackRate, 0.0)
        assertWarned("createMediaContent", "playbackRate", Double.NaN)

        session.updatePlaybackRate(2.0)
        session.updatePlaybackRate(Double.POSITIVE_INFINITY)
        assertEquals(2.0, session.playbackRate, 0.0)
        assertWarned("updatePlaybackRate", "playbackRate", Double.POSITIVE_INFINITY)
        assertEquals(warnings.toString(), 2, warnings.size)

        content.playingStart(start())
        assertEquals(2.0, events.last().parameters["playback_rate"])
        assertNoNonFinite()
    }

    @Test
    fun nonFiniteInputs_DoNotCrashRealGsonSizeCalculation() {
        val content = session().createMediaContent(MediaContentConfiguration(playbackRate = Double.NaN))
        content.playingStart(start(position = Double.NaN, duration = Double.POSITIVE_INFINITY))
        content.playingProgress(Double.NaN)
        content.seekStart(Double.NaN, targetSec = Double.NaN)
        lastSession!!.updatePlaybackRate(Double.NaN)

        val calculator = EventSizeCalculator(Component.provideGson(), mockk(), mockk())
        events.forEach {
            assertTrue(calculator.getSizeInBytes(it) in 1 until Long.MAX_VALUE)
        }
    }

    @Test
    fun controlCase_RawNaNParameter_ThrowsInRealGson() {
        val event = Event("generic", "MediaEvent", mutableMapOf<String, Any>("position_sec" to Double.NaN))
        val calculator = EventSizeCalculator(Component.provideGson(), mockk(), mockk())
        val thrown = runCatching { calculator.getSizeInBytes(event) }.exceptionOrNull()
        assertTrue("expected IllegalArgumentException, got $thrown", thrown is IllegalArgumentException)
    }
}
