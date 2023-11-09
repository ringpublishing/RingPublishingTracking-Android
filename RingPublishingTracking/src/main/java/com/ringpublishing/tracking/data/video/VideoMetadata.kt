package com.ringpublishing.tracking.data.video

/**
 * Video meta data holder
 * @param [publicationId] - Publication identifier in source system (CMS)
 * @param [contentId] - Content identifier in source system (CMS)
 * @param [isMainContentPiece] - Is video a main content piece? True if video player is a key part of the content; false otherwise
 * @param [videoStreamFormat] - Type of video stream format loaded into video player
 * @param [videoDuration] - Video duration (in seconds)
 * @param [videoContentCategory] - Video content category in terms of paid access to it
 * @param [videoAdsConfiguration] - Video ads configuration
 * @param [videoPlayerVersion] - Video player version name, for example 2.9.0
 */
data class VideoMetadata(
    val publicationId: String,
    val contentId: String,
    val isMainContentPiece: Boolean,
    val videoStreamFormat: VideoStreamFormat,
    val videoDuration: Int,
    val videoContentCategory: VideoContentCategory,
    val videoAdsConfiguration: VideoAdsConfiguration,
    val videoPlayerVersion: String
)
