package com.ringpublishing.tracking.internal.video

/**
 * Video event related params
 * @param [text] - event param text
 */
enum class VideoEvent(val text: String) {

    /**
     * Video player instance started loading video data
     */
    START("START"),

    /**
     * Video player started video playback (manually)
     */
    PLAYING_START("PLAYING_START"),

    /**
     * Video player started video playback (automatically)
     */
    PLAYING_AUTOSTART("PLAYING_AUTOSTART"),

    /**
     * Video player was paused
     */
    PAUSED("PAUSED"),

    /**
     * Video player was resumed after it was previously paused/stopped and it is playing video material
     */
    PLAYING("PLAYING"),

    /**
     * Video player is still playing material (after given period of time)
     */
    KEEP_PLAYING("KEEP-PLAYING"),

    /**
     * Video player finished playing video material
     */
    PLAYING_END("PLAYING_END")

}