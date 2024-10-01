package com.ringpublishing.tracking.data.audio

/**
 * Audio event related params
 * @param [text] - event param text
 */
enum class AudioEvent(val text: String) {

    /**
     * Audio player instance started loading audio data
     */
    START("START"),

    /**
     * Audio player started audio playback (manually)
     */
    PLAYING_START("PLAYING_START"),

    /**
     * Audio player started audio playback (automatically)
     */
    PLAYING_AUTOSTART("PLAYING_AUTOSTART"),

    /**
     * Audio player was paused
     */
    PAUSED("PAUSED"),

    /**
     * Audio player was resumed after it was previously paused/stopped and it is playing audio material
     */
    PLAYING("PLAYING"),

    /**
     * Audio player is still playing material (after given period of time)
     */
    KEEP_PLAYING("KEEP-PLAYING"),

    /**
     * Audio player finished playing audio material
     */
    PLAYING_END("PLAYING_END")
}
