package com.ringpublishing.tracking.data.media

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.factory.EventType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaSessionFixtureTest {

    private companion object {
        val UUID_V4 = Regex("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")
    }

    private val events = mutableListOf<Event>()

    private fun session(mediaType: MediaType, playerVariant: String? = null) =
        MediaSession(mediaType, playerVariant, { events.add(it) })

    @Test
    fun scenarioA_Audio_ProducesFixtureEvents() {
        val session = session(MediaType.AUDIO, "main")

        MediaScenarios.audio(session)

        assertMatchesFixture("scenario-a-audio.json", session)
    }

    @Test
    fun scenarioB_Video_ProducesFixtureEvents() {
        val session = session(MediaType.VIDEO, "inline")

        MediaScenarios.video(session)

        assertMatchesFixture("scenario-b-video.json", session)
    }

    @Test
    fun scenarioC_TtsEmpty_ProducesNoEvents() {
        val session = session(MediaType.TTS)

        MediaScenarios.ttsEmpty(session)

        assertMatchesFixture("scenario-c-tts-empty.json", session)
        assertEquals(0, events.size)
        assertEquals(false, session.isActive)
    }

    private fun number(value: Any) = (value as Number).toDouble()

    private fun assertMatchesFixture(fileName: String, session: MediaSession) {
        val stream = javaClass.classLoader!!.getResourceAsStream("fixtures/$fileName")!!
        val text = stream.bufferedReader().use { it.readText() }
        val expected = JsonParser().parse(text).asJsonArray

        assertEquals("event count", expected.size(), events.size)
        assertTrue(session.sessionId.matches(UUID_V4))
        assertEquals(36, session.sessionId.length)

        expected.forEachIndexed { index, element ->
            val event = events[index]
            assertEquals(AnalyticsSystem.GENERIC.text, event.analyticsSystemName)
            assertEquals(EventType.MEDIA.text, event.name)
            assertEquals("MediaEvent", event.name)
            assertParameters(index, element.asJsonObject, event.parameters, session.sessionId)
        }
    }

    private fun assertParameters(index: Int, expected: JsonObject, actual: Map<String, Any>, sessionId: String) {
        val label = "event #${index + 1} (${expected["event_type"]})"
        assertEquals("$label key set", expected.keySet().toSortedSet(), actual.keys.toSortedSet())

        expected.entrySet().forEach { entry ->
            val key = entry.key
            val primitive = entry.value.asJsonPrimitive
            val actualValue = actual.getValue(key)
            when {
                key == "media_session_id" -> {
                    assertEquals("<uuid>", primitive.asString)
                    assertEquals("$label $key", sessionId, actualValue)
                }
                primitive.isBoolean -> assertEquals("$label $key", primitive.asBoolean, actualValue)
                primitive.isNumber -> assertEquals("$label $key", primitive.asDouble, number(actualValue), 0.0)
                else -> assertEquals("$label $key", primitive.asString, actualValue)
            }
        }
    }
}
