package com.ringpublishing.tracking.data.media

/**
 * Why the playback ended
 * @param [text] - value sent to the backend
 */
enum class MediaPlaybackEndReason(val text: String) {

    /**
     * Content was played to the end
     */
    NATURAL_COMPLETION("natural_completion"),

    /**
     * User stopped the playback
     */
    USER_STOPPED("user_stopped"),

    /**
     * Playback failed
     */
    ERROR("error"),

    /**
     * Content was skipped
     */
    SKIPPED("skipped")
}
