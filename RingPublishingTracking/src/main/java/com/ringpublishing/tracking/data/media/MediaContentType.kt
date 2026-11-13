package com.ringpublishing.tracking.data.media

/**
 * Role of the played content
 * @param [text] - value sent to the backend
 */
enum class MediaContentType(val text: String) {

    /**
     * Advertisement. Report it with 'mediaContentSubtype' and 'relatedMediaId'
     */
    AD("ad"),

    /**
     * Main content
     */
    MAIN("main"),

    /**
     * Teaser of the main content
     */
    TEASER("teaser"),

    /**
     * Outro played after the main content
     */
    OUTRO("outro"),

    /**
     * Short bumper played between contents
     */
    BUMPER("bumper")
}
