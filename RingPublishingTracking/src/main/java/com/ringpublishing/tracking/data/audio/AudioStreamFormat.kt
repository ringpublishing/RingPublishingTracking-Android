package com.ringpublishing.tracking.data.audio

/**
 * Audio stream format extension
 * @param [text] - format extension text
 */
enum class AudioStreamFormat(val text: String) {

    /**
     * MPEG-3
     */
    MP3("mp3"),

    /**
     * HTTP Live Streaming
     */
    HLS("hls"),
}
