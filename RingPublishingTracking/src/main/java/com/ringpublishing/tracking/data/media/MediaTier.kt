package com.ringpublishing.tracking.data.media

/**
 * Tier the played content belongs to
 * @param [text] - value sent to the backend
 */
enum class MediaTier(val text: String) {

    /**
     * Free content
     */
    FREE("free"),

    /**
     * Content of the basic tier
     */
    BASIC("basic"),

    /**
     * Content of the premium tier
     */
    PREMIUM("premium"),

    /**
     * Exclusive content
     */
    EXCLUSIVE("exclusive")
}
