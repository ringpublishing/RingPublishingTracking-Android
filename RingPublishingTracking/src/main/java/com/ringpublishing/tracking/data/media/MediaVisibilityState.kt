package com.ringpublishing.tracking.data.media

/**
 * Visibility of the player
 * @param [text] - value sent to the backend
 */
enum class MediaVisibilityState(val text: String) {

    /**
     * Player is visible on the screen
     */
    VISIBLE("visible"),

    /**
     * Player is not visible on the screen
     */
    HIDDEN("hidden"),

    /**
     * Playback continues while the application is in the background
     */
    BACKGROUND("background"),

    /**
     * Player is shown in picture in picture mode
     */
    PIP("pip")
}
