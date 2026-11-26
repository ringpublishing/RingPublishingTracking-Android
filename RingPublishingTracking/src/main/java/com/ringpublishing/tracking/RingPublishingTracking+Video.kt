package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.video.VideoEvent
import com.ringpublishing.tracking.data.video.VideoMetadata
import com.ringpublishing.tracking.data.video.VideoState

/**
 * Reports Video player event
 * @param [videoEvent]: Type of Video event to report
 * @param [videoMetadata]: Video metadata
 * @param [videoState]: Currently played video state
 */
@Deprecated("Sends the legacy VidEvent. Use createMediaSession(mediaType, playerVariant) to report MediaEvent.")
fun RingPublishingTracking.reportVideoEvent(
    videoEvent: VideoEvent,
    videoMetadata: VideoMetadata,
    videoState: VideoState
) {
    reportEvent(videoEventsFactory.createVideoEvent(videoEvent, videoMetadata, videoState))
}
