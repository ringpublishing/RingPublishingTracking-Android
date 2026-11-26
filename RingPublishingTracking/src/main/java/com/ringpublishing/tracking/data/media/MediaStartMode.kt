package com.ringpublishing.tracking.data.media

/**
 * Sound state at the moment the playback started
 * @param [text] - value sent to the backend
 */
enum class MediaStartMode(val text: String) {

    /**
     * Playback started muted
     */
    MUTED_START("muted_start"),

    /**
     * Playback started with sound
     */
    NORMAL_START("normal_start")
}
