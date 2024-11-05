/*
 * Created by Daniel Całka on 10/01/24, 1:49 PM
 * Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.audio.AudioContentCategory
import com.ringpublishing.tracking.data.audio.AudioEvent
import com.ringpublishing.tracking.data.audio.AudioMetadata
import com.ringpublishing.tracking.data.audio.AudioOutput
import com.ringpublishing.tracking.data.audio.AudioPlayerVisibilityState
import com.ringpublishing.tracking.data.audio.AudioState
import com.ringpublishing.tracking.data.audio.AudioStreamFormat
import com.ringpublishing.tracking.internal.audio.AudioEventParam
import com.ringpublishing.tracking.internal.factory.AudioEventsFactory
import com.ringpublishing.tracking.internal.factory.EventType
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AudioEventsFactoryTest {

    private val gson = GsonBuilder().create()

    private val sampleAudioMetadata = AudioMetadata(
        contentId = "12167",
        contentTitle = "Bartosz Kwolek: siatkówka nie jest całym moim życiem",
        contentSeriesId = "67",
        contentSeriesTitle = "W cieniu sportu",
        mediaType = "podcast",
        audioDuration = 3722,
        audioStreamFormat = AudioStreamFormat.MP3,
        isContentFragment = false,
        audioContentCategory = AudioContentCategory.FREE,
        audioPlayerVersion = "1.0.0"
    )

    private val sampleAudioState = AudioState(
        currentTime = (System.currentTimeMillis() / 1000).toInt(),
        currentBitrate = 360,
        visibilityState = AudioPlayerVisibilityState.BACKGROUND,
        audioOutput = AudioOutput.BLUETOOTH
    )

    @Before
    fun `Bypass android_util_Base64 to java_util_Base64`() {
        mockkStatic(Base64::class)
        val arraySlot = slot<ByteArray>()

        every {
            Base64.encodeToString(capture(arraySlot), Base64.NO_WRAP)
        } answers {
            java.util.Base64.getEncoder().encodeToString(arraySlot.captured)
        }
    }

    @Test
    fun createAudioEvent_StringParameters_ThenParametersInEvent() {
        val eventsFactory = AudioEventsFactory(gson)
        val event = eventsFactory.createAudioEvent(AudioEvent.START, sampleAudioMetadata, sampleAudioState)

        Assert.assertTrue(event.parameters.isNotEmpty())
    }

    @Test
    fun createAudioEvent_WhenEventIsNotStart_ThenAudioNotInEvent() {
        val eventsFactory = AudioEventsFactory(gson)
        val event = eventsFactory.createAudioEvent(AudioEvent.PAUSED, sampleAudioMetadata, sampleAudioState)

        Assert.assertFalse(event.parameters.containsKey(AudioEventParam.AUDIO.text))
    }

    @Test
    fun createAudioEvent_WhenEventIsStart_ThenAudioInEvent() {
        val eventsFactory = AudioEventsFactory(gson)
        val event = eventsFactory.createAudioEvent(AudioEvent.START, sampleAudioMetadata, sampleAudioState)

        Assert.assertTrue(event.parameters.containsKey(AudioEventParam.AUDIO.text))
    }

    @Test
    fun createMultipleAudioEvents_WhenOneContentId_ThenCorrectEventCounter() {
        val eventsFactory = AudioEventsFactory(gson)

        val event0 = eventsFactory.createAudioEvent(AudioEvent.PLAYING, sampleAudioMetadata, sampleAudioState)
        val event1 = eventsFactory.createAudioEvent(AudioEvent.PLAYING, sampleAudioMetadata, sampleAudioState)
        val event2 = eventsFactory.createAudioEvent(AudioEvent.PLAYING, sampleAudioMetadata, sampleAudioState)
        val event3 = eventsFactory.createAudioEvent(AudioEvent.START, sampleAudioMetadata, sampleAudioState)

        Assert.assertEquals(event0.parameters[AudioEventParam.COUNTER.text], 0)
        Assert.assertEquals(event1.parameters[AudioEventParam.COUNTER.text], 1)
        Assert.assertEquals(event2.parameters[AudioEventParam.COUNTER.text], 2)
        Assert.assertEquals(event3.parameters[AudioEventParam.COUNTER.text], 0)
    }

    @Test
    fun createMultipleAudioEvents_WhenMultipleContentIds_ThenCorrectEventCounter() {
        val eventsFactory = AudioEventsFactory(gson)

        val sampleAudioMetadata0 = sampleAudioMetadata.copy(contentId = "0")
        val sampleAudioMetadata1 = sampleAudioMetadata.copy(contentId = "1")

        val event00 = eventsFactory.createAudioEvent(AudioEvent.START, sampleAudioMetadata0, sampleAudioState)
        val event01 = eventsFactory.createAudioEvent(AudioEvent.PLAYING, sampleAudioMetadata0, sampleAudioState)
        val event02 = eventsFactory.createAudioEvent(AudioEvent.PLAYING, sampleAudioMetadata0, sampleAudioState)

        val event10 = eventsFactory.createAudioEvent(AudioEvent.START, sampleAudioMetadata1, sampleAudioState)
        val event11 = eventsFactory.createAudioEvent(AudioEvent.PLAYING, sampleAudioMetadata1, sampleAudioState)
        val event12 = eventsFactory.createAudioEvent(AudioEvent.START, sampleAudioMetadata1, sampleAudioState)

        Assert.assertEquals(event00.parameters[AudioEventParam.COUNTER.text], 0)
        Assert.assertEquals(event01.parameters[AudioEventParam.COUNTER.text], 1)
        Assert.assertEquals(event02.parameters[AudioEventParam.COUNTER.text], 2)
        Assert.assertEquals(event10.parameters[AudioEventParam.COUNTER.text], 0)
        Assert.assertEquals(event11.parameters[AudioEventParam.COUNTER.text], 1)
        Assert.assertEquals(event12.parameters[AudioEventParam.COUNTER.text], 0)
    }

    @Test
    fun createAudioEvent_StringParameters_ThenParameterValues() {
        val eventsFactory = AudioEventsFactory(gson)
        val audioEvent = AudioEvent.START
        val event = eventsFactory.createAudioEvent(audioEvent, sampleAudioMetadata, sampleAudioState)
        val sampleData = "audio:${sampleAudioMetadata.contentId},${sampleAudioMetadata.contentId},${sampleAudioMetadata.audioStreamFormat.text},360"
        val sampleContextJson = "{\"context\":{\"visible\":\"background\",\"audio\":{\"output\":\"bluetooth\"}}}"
        val sampleContextJsonEncoded = Base64.encodeToString(sampleContextJson.toByteArray(), Base64.NO_WRAP)
        val isContentFragment = if (sampleAudioMetadata.isContentFragment) 1 else 0

        Assert.assertEquals(event.parameters[AudioEventParam.SELECTED_ELEMENT_NAME.text], audioEvent.text)
        Assert.assertEquals(event.parameters[AudioEventParam.EVENT_TYPE.text], EventType.VIDEO.text)
        Assert.assertEquals(event.parameters[AudioEventParam.CONTENT_ID.text], sampleAudioMetadata.contentId)
        Assert.assertEquals(event.parameters[AudioEventParam.DURATION.text], sampleAudioMetadata.audioDuration)
        Assert.assertEquals(event.parameters[AudioEventParam.CURRENT_TIME.text], sampleAudioState.currentTime)
        Assert.assertEquals(event.parameters[AudioEventParam.AUDIO_PARAMETERS.text], sampleData)
        Assert.assertEquals(event.parameters[AudioEventParam.COUNTER.text], 0)
        Assert.assertEquals(event.parameters[AudioEventParam.FORMAT.text], sampleAudioMetadata.audioStreamFormat.text)
        Assert.assertEquals(event.parameters[AudioEventParam.START_MODE.text], "normal")
        Assert.assertEquals(event.parameters[AudioEventParam.AUDIO_PLAYER_VERSION.text], sampleAudioMetadata.audioPlayerVersion)
        Assert.assertEquals(event.parameters[AudioEventParam.IS_CONTENT_FRAGMENT.text], isContentFragment)
        Assert.assertEquals(event.parameters[AudioEventParam.PLAYER_TYPE.text], "player")
        Assert.assertEquals(event.parameters[AudioEventParam.IS_MAIN_AUDIO.text], "mainAudio")
        Assert.assertEquals(event.parameters[AudioEventParam.AUDIO_CONTENT_CATEGORY.text], sampleAudioMetadata.audioContentCategory.text)
        Assert.assertEquals(event.parameters[AudioEventParam.CONTEXT.text], sampleContextJsonEncoded)
    }
}
