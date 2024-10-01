package com.ringpublishing.tracking.data.audio

/**
 * Audio content category params
 * @param [text] - param text
 */
enum class AudioContentCategory(val text: String) {

    /**
     * Audio content is free
     */
    FREE("free"),

    /**
     * Audio content is paid
     */
    PAID("paid"),
}
