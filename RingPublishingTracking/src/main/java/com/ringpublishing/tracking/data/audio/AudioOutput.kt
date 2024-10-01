package com.ringpublishing.tracking.data.audio

/**
 * Audio output type
 * @param [text] - type param text
 */
enum class AudioOutput(val text: String) {

    /**
     * Audio is being player by external bluetooth device
     */
    BLUETOOTH("bluetooth"),

    /**
     * Audio is being played by speakers
     */
    SPEAKERS("speakers"),

    /**
     * Audio is being played by headphones
     */
    HEADPHONES("headphones"),
}
