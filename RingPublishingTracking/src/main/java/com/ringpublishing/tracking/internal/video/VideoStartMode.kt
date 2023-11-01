package com.ringpublishing.tracking.internal.video

/**
 * Video start mode related params
 * @param [text] - mode param text
 */
enum class VideoStartMode(val text: String) {

    /**
     * Video was stared while muted
     */
    MUTED("ms"),

    /**
     * Video was started while unmuted
     */
    NORMAL("ns"),

    /**
     *  Video was started while muted but later was unmuted
     */
    WAS_MUTED("wm")

}