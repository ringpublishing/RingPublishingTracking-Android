package com.ringpublishing.tracking.data.media

/**
 * Subscription tier of the user
 * @param [text] - value sent to the backend
 */
enum class MediaSubscriptionTier(val text: String) {

    /**
     * No paid subscription
     */
    FREE("free"),

    /**
     * Basic subscription
     */
    BASIC("basic"),

    /**
     * Premium subscription
     */
    PREMIUM("premium"),

    /**
     * VIP subscription
     */
    VIP("vip")
}
