package com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.audio.AudioEvent
import com.ringpublishing.tracking.data.audio.AudioMediaType
import com.ringpublishing.tracking.data.audio.AudioMetadata
import com.ringpublishing.tracking.data.audio.AudioOutput
import com.ringpublishing.tracking.data.audio.AudioPlayerVisibilityState
import com.ringpublishing.tracking.data.audio.AudioState
import com.ringpublishing.tracking.internal.audio.AudioEventParam
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import java.util.UUID

private const val START_MODE = "normal"

private const val PLAYER_TYPE = "player"

private const val MAIN_AUDIO = "mainAudio"

internal class AudioEventsFactory(private val gson: Gson) {

    private val sessionTimestamps: MutableMap<String, String> = mutableMapOf()

    private val sessionCounters: MutableMap<String, Int> = mutableMapOf()

    fun createAudioEvent(audioEvent: AudioEvent, audioMetadata: AudioMetadata, audioState: AudioState): Event {

        val parameters = mutableMapOf<String, Any>().apply {
            this[AudioEventParam.SELECTED_ELEMENT_NAME.text] = audioEvent.text
            this[AudioEventParam.EVENT_TYPE.text] = EventType.VIDEO.text
            this[AudioEventParam.CURRENT_TIME.text] = audioState.currentTime
            this[AudioEventParam.AUDIO_PARAMETERS.text] = createAudioEventVCParameter(audioMetadata, audioState)
            this[AudioEventParam.TIMESTAMP.text] = getAndUpdateSessionTimestamp(audioMetadata.contentId, audioEvent)
            this[AudioEventParam.COUNTER.text] = getAndUpdateSessionCounter(audioMetadata.contentId, audioEvent)
            this[AudioEventParam.FORMAT.text] = audioMetadata.audioStreamFormat.text
            this[AudioEventParam.START_MODE.text] = START_MODE
            this[AudioEventParam.AUDIO_PLAYER_VERSION.text] = audioMetadata.audioPlayerVersion
            this[AudioEventParam.PLAYER_TYPE.text] = PLAYER_TYPE
            this[AudioEventParam.IS_MAIN_AUDIO.text] = MAIN_AUDIO
            this[AudioEventParam.AUDIO_CONTENT_CATEGORY.text] = audioMetadata.audioContentCategory.text
            this[AudioEventParam.CONTEXT.text] = createContextParam(audioState.visibilityState, audioState.audioOutput)
            this[AudioEventParam.IS_CONTENT_FRAGMENT.text] = if (audioMetadata.isContentFragment) 1 else 0

            audioMetadata.contentId.asUuidOrNull()?.let {
                this[AudioEventParam.CONTENT_ID.text] = it
            }

            audioMetadata.audioDuration?.let {
                this[AudioEventParam.DURATION.text] = it
            }

            if (audioEvent == AudioEvent.START || audioEvent == AudioEvent.PLAYING_START || audioEvent == AudioEvent.PLAYING_AUTOSTART) {
                this[AudioEventParam.AUDIO.text] = audioMetadata.createAudioJson()
            }
        }

        return Event(
            analyticsSystemName = AnalyticsSystem.KROPKA_STATS.text,
            name = EventType.VIDEO.text,
            parameters = parameters
        )
    }

    /**
     * Creates VC audio event parameter
     * Format: prefix:publicationId,type,bitrate
     * Example: "audio:23345.275928614,23345.275928614,mp3,320"
     */
    private fun createAudioEventVCParameter(metadata: AudioMetadata, audioState: AudioState): String {
        val contentId = if (metadata.mediaType == AudioMediaType.TTS) "6" else metadata.contentId
        return "audio:${contentId},${contentId},${metadata.audioStreamFormat.text},${audioState.currentBitrate}"
    }


    /**
     * Retrieves timestamp string from sessionTimestamps for given [contentId].
     * Creates one and puts in map if it does not exist or if [audioEvent] value == [AudioEvent.START]
     */
    private fun getAndUpdateSessionTimestamp(contentId: String, audioEvent: AudioEvent) =
        sessionTimestamps[contentId].let { savedTimestamp ->
            if (savedTimestamp == null || audioEvent == AudioEvent.START) {
                "${System.currentTimeMillis()}".also { sessionTimestamps[contentId] = it }
            } else {
                savedTimestamp
            }
        }

    /**
     * Retrieves and increment by 1 audio session counter for given [contentId].
     * If counter is not found or [audioEvent] value == [AudioEvent.START] new counter is saved with value 0.
     */
    private fun getAndUpdateSessionCounter(contentId: String, audioEvent: AudioEvent) =
        sessionCounters[contentId].let { savedCounter ->
            if (savedCounter == null || audioEvent == AudioEvent.START) 0
            else savedCounter + 1
        }.also {
            sessionCounters[contentId] = it
        }

    /**
     * Creates json context param string based on [AudioContextParam] class structure, containing given [AudioPlayerVisibilityState] and [AudioOutputContext] values
     * and encodes this json with Base64.
     * example json to encode: {"context":{"visible":"background", "audio":{"output":"headphones"}}}
     */
    private fun createContextParam(visibility: AudioPlayerVisibilityState, output: AudioOutput): String =
        gson.toJson(
            AudioContextParam(AudioContext(visibility.text, AudioOutputContext(output.text)))
        ).let { json ->
            Base64.encodeToString(json.toByteArray(), Base64.NO_WRAP)
        }

    /**
     * Creates json string based on [AudioEventData] class structure
     */
    private fun AudioMetadata.createAudioJson() = gson.toJson(
        AudioEventData(
            id = contentId,
            contentSeriesId = contentSeriesId,
            title = contentTitle,
            seriesTitle = contentSeriesTitle,
            mediaType = mediaType.text
        )
    )

    /**
     * Checks if String is proper UUID format and returns it or null if it is not.
     */
    private fun String.asUuidOrNull() = runCatching {
        UUID.fromString(this)
        this
    }.getOrNull()

    data class AudioContextParam(val context: AudioContext)

    data class AudioContext(val visible: String, val audio: AudioOutputContext)

    data class AudioOutputContext(val output: String)

    data class AudioEventData(
        @SerializedName("id") val id: String,
        @SerializedName("series_id") val contentSeriesId: String?,
        @SerializedName("title") val title: String,
        @SerializedName("series_title") val seriesTitle: String?,
        @SerializedName("media_type") val mediaType: String
    )
}
