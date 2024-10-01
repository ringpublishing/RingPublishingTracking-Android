/*
 * Created by Daniel Całka on 10/01/24, 12:22 PM
 * Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.sample

import com.ringpublishing.tracking.data.audio.AudioContentCategory
import com.ringpublishing.tracking.data.audio.AudioMetadata
import com.ringpublishing.tracking.data.audio.AudioOutput
import com.ringpublishing.tracking.data.audio.AudioPlayerVisibilityState
import com.ringpublishing.tracking.data.audio.AudioState
import com.ringpublishing.tracking.data.audio.AudioStreamFormat

val sampleAudioMetadata = AudioMetadata(
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

val sampleAudioState = AudioState(
    currentTime = (System.currentTimeMillis() / 1000).toInt(),
    currentBitrate = "360",
    visibilityState = AudioPlayerVisibilityState.BACKGROUND,
    audioOutput = AudioOutput.BLUETOOTH
)
