package com.ringpublishing.tracking.data.media

/**
 * Kind of media played by the player that owns a [MediaSession]
 * @param [text] - value sent to the backend
 */
enum class MediaType(val text: String) {

    /**
     * Video player
     */
    VIDEO("video"),

    /**
     * Audio player
     */
    AUDIO("audio"),

    /**
     * Text to speech player
     */
    TTS("tts")
}
