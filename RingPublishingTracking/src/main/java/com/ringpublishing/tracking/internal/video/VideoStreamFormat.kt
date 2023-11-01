package com.ringpublishing.tracking.internal.video

/**
 * Video stream format related params
 * @param [text] - format param text
 */
enum class VideoStreamFormat(val text: String) {

    /**
     * Flash Video
     */
    FLV("flv"),

    /**
     * MPEG-4
     */
    MP4("mp4"),

    /**
     * Manifest with video stream, for example .m3u8
     */
    MANIFEST("manifest"),

    /**
     * Manifest with live video stream, for example .m3u8
     */
    LIVE_MANIFEST("manifestlive"),

    /**
     * 3GP (.3gp)
     */
    THIRD_GENERATION_PARTNERSHIP("3gp"),

    /**
     * HTTP Dynamic Streaming
     */
    HDS("hds"),

    /**
     * HTTP Live Streaming
     */
    HLS("hls"),

    /**
     * Real-Time Messaging Protocol
     */
    RTSP("rtsp")
}