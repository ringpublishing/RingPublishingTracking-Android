package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.audio.AudioEvent
import com.ringpublishing.tracking.data.audio.AudioMetadata
import com.ringpublishing.tracking.data.audio.AudioState

/**
 * Reports Audio player event
 * @param [audioEvent]: Type of audio event to report
 * @param [audioMetadata]: Audio metadata @see [AudioMetadata]
 * @param [audioState]: Currently played audio state @see [AudioState]
 */
@Deprecated("Sends the legacy VidEvent. Use createMediaSession(mediaType, playerVariant) to report MediaEvent.")
fun RingPublishingTracking.reportAudioEvent(
    audioEvent: AudioEvent,
    audioMetadata: AudioMetadata,
    audioState: AudioState
) {
    reportEvent(audioEventsFactory.createAudioEvent(audioEvent, audioMetadata, audioState))
}
