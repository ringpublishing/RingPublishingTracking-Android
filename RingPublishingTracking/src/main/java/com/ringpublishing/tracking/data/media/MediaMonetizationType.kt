package com.ringpublishing.tracking.data.media

/**
 * How the played content is monetized
 * @param [text] - value sent to the backend
 */
enum class MediaMonetizationType(val text: String) {

    /**
     * Free content
     */
    FREE("free"),

    /**
     * Content financed by advertisements
     */
    AD("ad"),

    /**
     * Content bought separately
     */
    TRANSACTIONAL("transactional"),

    /**
     * Content available with a subscription
     */
    SUBSCRIPTION("subscription")
}
