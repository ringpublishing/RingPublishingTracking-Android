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

    private fun assertSchemaPositions() {
        val required = setOf(
            "MEDIA_PLAYING_START", "MEDIA_PLAYING_PROGRESS", "MEDIA_PLAYING_END", "MEDIA_PAUSE", "MEDIA_RESUME",
            "MEDIA_SEEK_START", "MEDIA_SEEK_END", "MEDIA_ERROR"
        )
        events.filter { it.parameters["event_type"] in required }.forEach {
            assertTrue("no position_sec in ${it.parameters}", it.parameters["position_sec"] is Double)
        }
    }

    @Test
    fun positionSec_NaN_UsesLastPositionOfContent() {
        val content = playing()
        content.playingProgress(12.0)

        content.playingProgress(Double.NaN)

        assertEquals("MEDIA_PLAYING_PROGRESS", events.last().parameters["event_type"])
        assertEquals(12.0, events.last().parameters["position_sec"])
        assertWarned("playingProgress", "positionSec", Double.NaN)
        assertSingleWarning()
        assertNoNonFinite()
        assertSchemaPositions()
    }

    @Test
    fun positionSec_NonFinite_InEveryMethod_UsesSubstitute() {
        val content = playing()
        content.playingProgress(12.0)
        val calls = listOf<Pair<String, () -> Unit>>(
            "playingProgress" to { content.playingProgress(Double.POSITIVE_INFINITY) },
            "mute" to { content.mute(Double.NaN) },
            "unmute" to { content.unmute(Double.NaN) },
            "outputChange" to { content.outputChange(Double.NaN, MediaAudioOutput.CAR) },
            "visibilityChange" to { content.visibilityChange(Double.NaN, MediaVisibilityState.PIP) },
            "error" to { content.error(Double.NaN, "E1", "boom") },
            "seekStart" to { content.seekStart(Double.NEGATIVE_INFINITY) },
            "seekEnd" to { content.seekEnd(Double.NaN) },
            "pause" to { content.pause(Double.NaN, MediaPauseReason.USER_ACTION) },
            "resume" to { content.resume(Double.NaN) },
            "playingEnd" to { content.playingEnd(Double.NaN, MediaPlaybackEndReason.USER_STOPPED) }
        )

        calls.forEach { (method, call) ->
            val before = events.size
            warnings.clear()
            call()
            assertEquals("$method sends one event", before + 1, events.size)
            assertEquals("$method position", 12.0, events.last().parameters["position_sec"])
            assertEquals("$method warns once: $warnings", 1, warnings.size)
            assertTrue(warnings[0].contains("$method: 'positionSec' must be a finite number"))
        }
        assertNoNonFinite()
        assertSchemaPositions()
    }

    @Test
    fun positionSec_NaN_InPlayingStart_IsZeroForContentWithoutPosition() {
        val content = session().createMediaContent()

        content.playingStart(start(position = Double.NaN))

        assertEquals("MEDIA_PLAYING_START", events.last().parameters["event_type"])
        assertEquals(0.0, events.last().parameters["position_sec"])
        assertWarned("playingStart", "positionSec", Double.NaN)
        assertSingleWarning()
        assertNoNonFinite()
    }

    @Test
    fun positionSec_NaN_SubstituteBecomesLastPosition() {
        val content = playing()
        content.playingProgress(Double.NaN)
        content.playingProgress(Double.NaN)

        assertEquals(listOf(0.0, 0.0), events.takeLast(2).map { it.parameters["position_sec"] })

        content.playingProgress(30.0)
        content.pause(Double.NaN, MediaPauseReason.USER_ACTION)
        assertEquals(30.0, events.last().parameters["position_sec"])
    }

    @Test
    fun positionSec_NaN_InSeekEnd_LandsOnSeekTargetAndHeldResumeFollows() {
        val content = playing()
        content.playingProgress(5.0)
        content.pause(6.0, MediaPauseReason.USER_ACTION)
        content.seekStart(6.0, targetSec = 40.0)
        content.resume(Double.NaN)
        warnings.clear()
        val before = events.size

        content.seekEnd(Double.NaN)

        val sent = events.drop(before)
        assertEquals(listOf("MEDIA_SEEK_END", "MEDIA_RESUME"), sent.map { it.parameters["event_type"] })
        sent.forEach { assertEquals(40.0, it.parameters["position_sec"]) }
        assertSingleWarning()
        assertSchemaPositions()
    }

    @Test
    fun positionSec_NaN_DuringSeekWithTarget_UsesTarget() {
        val content = playing()
        content.playingProgress(5.0)
        content.seekStart(6.0, targetSec = 40.0)

        content.pause(Double.NaN, MediaPauseReason.USER_ACTION)

        assertEquals(40.0, events.last().parameters["position_sec"])
    }

    @Test
    fun positionSec_NaN_AdPausedContentKeepsItsOwnLastPosition() {
        val content = playing()
        content.playingProgress(12.0)
        content.playingProgress(Double.NaN)

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
    fun durationSec_NaN_IsZeroInThisAndLaterEvents() {
        val content = session().createMediaContent()

        content.playingStart(start(duration = Double.NaN))
        content.playingProgress(1.0)

        val sent = events.filter { it.parameters["event_type"] != "MEDIA_SESSION_START" }
        assertEquals(2, sent.size)
        sent.forEach { assertEquals(0.0, it.parameters["duration_sec"]) }
        assertWarned("playingStart", "durationSec", Double.NaN)
        assertSingleWarning()
        assertNoNonFinite()
    }

    @Test
    fun durationSec_Infinite_IsZero() {
        session().createMediaContent().playingStart(start(duration = Double.NEGATIVE_INFINITY))

        assertEquals(0.0, events.last().parameters["duration_sec"])
        assertWarned("playingStart", "durationSec", Double.NEGATIVE_INFINITY)
        assertSingleWarning()
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
