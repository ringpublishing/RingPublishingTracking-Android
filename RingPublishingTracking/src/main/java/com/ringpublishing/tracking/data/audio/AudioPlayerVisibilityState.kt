package com.ringpublishing.tracking.data.audio

/**
 * Audio player visibility state related params
 * @param [text] - format param text
 */
enum class AudioPlayerVisibilityState(val text: String) {

    /**
     * Audio player is visible for the user
     */
    VISIBLE("visible"),

    /**
     * Audio player is in background (system player is visible)
     */
    BACKGROUND("background")
}
