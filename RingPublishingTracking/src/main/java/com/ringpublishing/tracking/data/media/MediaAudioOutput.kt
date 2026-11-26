package com.ringpublishing.tracking.data.media

/**
 * Device the sound is played on
 * @param [text] - value sent to the backend
 */
enum class MediaAudioOutput(val text: String) {

    /**
     * Built in speaker of the device
     */
    SPEAKER("speaker"),

    /**
     * Wired headphones
     */
    HEADPHONES("headphones"),

    /**
     * Bluetooth device
     */
    BLUETOOTH("bluetooth"),

    /**
     * Car audio system
     */
    CAR("car"),

    /**
     * Sound is casted with Chromecast
     */
    CHROMECAST("chromecast"),

    /**
     * Other external device
     */
    EXTERNAL("external")
}
