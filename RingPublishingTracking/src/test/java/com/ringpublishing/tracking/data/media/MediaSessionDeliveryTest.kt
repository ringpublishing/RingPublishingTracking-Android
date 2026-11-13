package com.ringpublishing.tracking.data.media

import com.ringpublishing.tracking.data.Event
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MediaSessionDeliveryTest {

    private val events = mutableListOf<Event>()

    private fun parameters(position: Double = 0.0) = MediaPlayingStartParameters(
        mediaId = "m1",
        durationSec = 100.0,
        mediaContentType = MediaContentType.MAIN,
        playbackIntent = MediaPlaybackIntent.AUTOPLAY,
        positionSec = position
    )

    @Test
    fun callDuringSlowDelivery_ReturnsWithoutWaitingAndKeepsOrder() {
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val session = MediaSession(MediaType.VIDEO, null, { event ->
            if (event.parameters["event_type"] == "MEDIA_PLAYING_PROGRESS") {
                entered.countDown()
                release.await(5, TimeUnit.SECONDS)
            }
            synchronized(events) { events.add(event) }
        })
        val content = session.createMediaContent()
        content.playingStart(parameters())

        val threadB = Thread { content.playingProgress(1.0) }
        threadB.start()
        assertTrue(entered.await(5, TimeUnit.SECONDS))

        val threadC = Thread { content.mute(2.0) }
        threadC.start()
        threadC.join(2000)
        val cFinished = !threadC.isAlive
        release.countDown()
        threadB.join(5000)
        threadC.join(5000)

        assertTrue("a call made during another thread's delivery must not wait for it", cFinished)
        val tail = events.takeLast(2)
        assertEquals(listOf("MEDIA_PLAYING_PROGRESS", "MEDIA_MUTE"), tail.map { it.parameters["event_type"] })
        assertEquals(listOf(3, 4), tail.map { it.parameters["event_ordinal"] })
        assertEquals(events.indices.map { it + 1 }, events.map { it.parameters["event_ordinal"] })
    }

    @Test
    fun sinkCallingBackIntoSession_OnlyEnqueuesAndLoopDeliversInOrder() {
        lateinit var content: MediaContent
        var reentered = false
        val session = MediaSession(MediaType.AUDIO, null, { event ->
            val depth = events.size
            events.add(event)
            if (event.parameters["event_type"] == "MEDIA_PLAYING_START" && !reentered) {
                reentered = true
                content.playingProgress(5.0)
                // Not delivered synchronously from inside the sink.
                assertEquals(depth + 1, events.size)
            }
        })
        content = session.createMediaContent()
        content.playingStart(parameters())

        assertEquals(
            listOf("MEDIA_SESSION_START", "MEDIA_PLAYING_START", "MEDIA_PLAYING_PROGRESS"),
            events.map { it.parameters["event_type"] }
        )
        assertEquals(listOf(1, 2, 3), events.map { it.parameters["event_ordinal"] })
        assertFalse(events.isEmpty())
    }

    @Test
    fun sinkThrowing_DoesNotStrandLaterEvents() {
        val session = MediaSession(MediaType.AUDIO, null, { event ->
            if (event.parameters["event_type"] == "MEDIA_PLAYING_START") throw IllegalStateException("sink failure")
            events.add(event)
        })
        val content = session.createMediaContent()

        try {
            content.playingStart(parameters())
        } catch (expected: IllegalStateException) {
            // The sink failure reaches the caller.
        }
        content.playingProgress(1.0)

        assertEquals(listOf(1, 3), events.map { it.parameters["event_ordinal"] })
    }
}
