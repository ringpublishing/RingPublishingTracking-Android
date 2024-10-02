package com.ringpublishing.tracking.data.audio

/**
 * Audio output type
 * @param [text] - type param text
 */
enum class AudioOutput(val text: String) {

    /**
     * Audio is being played on mobile device
     */
    MOBILE("mobile"),

    /**
     * Audio is being casted by chromecast
     */
    CHROMECAST("chromecast"),

    /**
     * Audio is being played on car audio system
     */
    CAR("car"),

    /**
     * Audio is being played on bluetooth device
     */
    BLUETOOTH("bluetooth"),

    /**
     * Audio is being played on external device
     */
    EXTERNAL("external")
}
