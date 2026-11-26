package com.ringpublishing.tracking.data.media

/**
 * What caused the playback to start
 * @param [text] - value sent to the backend
 */
enum class MediaPlaybackIntent(val text: String) {

    /**
     * Playback started automatically
     */
    AUTOPLAY("autoplay"),

    /**
     * User started the playback
     */
    USER_INITIATED("user_initiated"),

    /**
     * Playback continues the previous content, for example the next item of a playlist
     */
    CONTINUATION("continuation")
}
