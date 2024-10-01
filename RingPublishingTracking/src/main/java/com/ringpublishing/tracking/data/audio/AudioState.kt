package com.ringpublishing.tracking.data.audio

/**
 * Audio state data holder
 * @param [currentTime] - Current audio material timestamp shown in audio player
 * @param [currentBitrate] - Current audio material bitrate (in kbps)
 * @param [visibilityState] - Current audio player visibility @see [AudioPlayerVisibilityState]
 * @param [audioOutput] - Audio output type @see [AudioOutput]
 */
data class AudioState(
    val currentTime: Int,
    val currentBitrate: String,
    val visibilityState: AudioPlayerVisibilityState,
    val audioOutput: AudioOutput
)
