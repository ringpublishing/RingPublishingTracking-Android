package com.ringpublishing.tracking.data.media

/**
 * Why the playback was paused
 * @param [text] - value sent to the backend
 */
enum class MediaPauseReason(val text: String) {

    /**
     * User paused the playback
     */
    USER_ACTION("user_action"),

    /**
     * Playback paused because of the network, for example buffering
     */
    NETWORK("network"),

    /**
     * Application paused the playback
     */
    PROGRAMMATIC("programmatic"),

    /**
     * Playback paused for an advertisement
     */
    AD("ad")
}
