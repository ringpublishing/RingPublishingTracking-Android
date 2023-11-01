package com.ringpublishing.tracking.internal.video

/**
 * Video ads configuration related params
 * @param [text] - configuration param text
 */
enum class VideoAdsConfiguration(val text: String?) {

    /**
     * Ads are enabled and can be played
     */
    ENABLED(null),

    /**
     * Ads are disabled by player configuration
     */
    DISABLED_BY_CONFIGURATION("ea"),

    /**
     * Ads are disabled by player configuration
     */
    DISABLED_BY_VIDEO_CONTENT("nb"),

    /**
     * Ads are temporarily disabled
     */
    DISABLED_WITH_GRACE_PERIOD("gp")

}