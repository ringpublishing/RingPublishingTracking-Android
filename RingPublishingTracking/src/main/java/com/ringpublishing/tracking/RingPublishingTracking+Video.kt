package com.ringpublishing.tracking

import com.ringpublishing.tracking.internal.video.VideoEvent
import com.ringpublishing.tracking.internal.video.VideoMetadata
import com.ringpublishing.tracking.internal.video.VideoState

/**
 * Reports Video player event
 * @param [videoEvent]: Type of Video event to report
 * @param [videoMetadata]: Video metadata
 * @param [videoState]: Currently played video state
 */
fun RingPublishingTracking.reportVideoEvent(
    videoEvent: VideoEvent,
    videoMetadata: VideoMetadata,
    videoState: VideoState
) {
    reportEvent(videoEventsFactory.createVideoEvent(videoEvent, videoMetadata, videoState))
}