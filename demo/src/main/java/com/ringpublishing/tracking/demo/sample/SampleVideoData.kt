package com.ringpublishing.tracking.demo.sample

import com.ringpublishing.tracking.data.video.VideoAdsConfiguration
import com.ringpublishing.tracking.data.video.VideoContentCategory
import com.ringpublishing.tracking.data.video.VideoMetadata
import com.ringpublishing.tracking.data.video.VideoStartMode
import com.ringpublishing.tracking.data.video.VideoState
import com.ringpublishing.tracking.data.video.VideoStreamFormat
import com.ringpublishing.tracking.data.video.VideoVisibilityState

val sampleVideoMetadata = VideoMetadata(
    publicationId = "2305009.546843861",
    contentId = "7f5d98d1-6a69-4f08-89b9-97d55e2372ba",
    isMainContentPiece = true,
    videoStreamFormat = VideoStreamFormat.HLS,
    videoDuration = 123,
    videoContentCategory = VideoContentCategory.FREE,
    videoAdsConfiguration = VideoAdsConfiguration.ENABLED,
    videoPlayerVersion = "1.0.2"
)

val sampleVideoState = VideoState(
    currentTime = (System.currentTimeMillis() / 1000).toInt(),
    currentBitrate = "1800",
    isMuted = false,
    visibilityState = VideoVisibilityState.VISIBLE,
    startMode = VideoStartMode.NORMAL
)
