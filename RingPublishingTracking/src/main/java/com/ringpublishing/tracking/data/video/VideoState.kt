package com.ringpublishing.tracking.data.video

/**
 * Video state data holder
 * @param [currentTime] - Current video material timestamp shown in video player
 * @param [currentBitrate] - Current video material bitrate (in kbps)
 * @param [isMuted] - Is video currently muted
 * @param [visibilityState] - Current video visibility @see [VideoVisibility]
 * @param [startMode] - Video start mode @see [VideoStartMode]
 */
data class VideoState(
    val currentTime: Int,
    val currentBitrate: String,
    val isMuted: Boolean,
    val visibilityState: VideoVisibilityState,
    val startMode: VideoStartMode
)
