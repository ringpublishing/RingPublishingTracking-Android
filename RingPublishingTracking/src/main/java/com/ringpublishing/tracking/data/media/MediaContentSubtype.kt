package com.ringpublishing.tracking.data.media

/**
 * Position of an advertisement relative to the main content
 * @param [text] - value sent to the backend
 */
enum class MediaContentSubtype(val text: String) {

    /**
     * Advertisement played before the main content
     */
    PREROLL("preroll"),

    /**
     * Advertisement played in the middle of the main content
     */
    MIDROLL("midroll"),

    /**
     * Advertisement played after the main content
     */
    POSTROLL("postroll")
}
