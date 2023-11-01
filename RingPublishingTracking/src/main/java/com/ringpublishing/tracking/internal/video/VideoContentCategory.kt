package com.ringpublishing.tracking.internal.video

/**
 * Video content category related params
 * @param [text] - category param text
 */
enum class VideoContentCategory(val text: String) {

    /**
     * Video content is free
     */
    FREE("free"),

    /**
     * Video content was purchased by one time purchase
     */
    VOD_PAY_PER_VIEW("tvod"),

    /**
     * Video content was purchased using premium subscription
     */
    VOID_SUBSCRIPTION("svod")

}