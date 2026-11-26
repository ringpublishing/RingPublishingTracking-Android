package com.ringpublishing.tracking.data.media

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.factory.MediaEventsFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MediaUserTypeTest {

    private val configuration = ConfigurationManager()
    private val events = mutableListOf<Event>()
    private val session = MediaSession(
        MediaType.AUDIO,
        null,
        { events.add(it) },
        MediaEventsFactory { configuration.getUserData().isActiveSubscriber }
    )

    private fun start(mediaId: String) = MediaPlayingStartParameters(
        mediaId = mediaId,
        durationSec = 100.0,
        mediaContentType = MediaContentType.MAIN,
        playbackIntent = MediaPlaybackIntent.USER_INITIATED,
        positionSec = 0.0
    )

    @Test
    fun activeSubscriber_EventsCarrySubscriber() {
        configuration.updateIsActiveSubscriber(true)

        session.createMediaContent().playingStart(start("m1"))

        assertEquals(2, events.size)
        events.forEach { assertEquals("subscriber", it.parameters["user_type"]) }
    }

    @Test
    fun notSubscriber_EventsHaveNoUserType() {
        configuration.updateIsActiveSubscriber(false)

        session.createMediaContent().playingStart(start("m1"))

        assertEquals(2, events.size)
        events.forEach { assertFalse(it.parameters.containsKey("user_type")) }
    }

    @Test
    fun flagNeverSet_EventsHaveNoUserType() {
        session.createMediaContent().playingStart(start("m1"))

        assertEquals(2, events.size)
        events.forEach { assertFalse(it.parameters.containsKey("user_type")) }
    }

    @Test
    fun flagChangeBetweenEvents_ShowsInTheNextEvent() {
        configuration.updateIsActiveSubscriber(false)
        val content = session.createMediaContent()
        content.playingStart(start("m1"))

        configuration.updateIsActiveSubscriber(true)
        content.pause(10.0, MediaPauseReason.USER_ACTION)

        assertFalse(events[0].parameters.containsKey("user_type"))
        assertFalse(events[1].parameters.containsKey("user_type"))
        assertEquals(3, events.size)
        assertEquals("subscriber", events.last().parameters["user_type"])
    }
}
