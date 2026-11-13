package com.ringpublishing.tracking.internal.media

import com.ringpublishing.tracking.internal.log.Logger

/**
 * Checks values of the media API. A violation is logged as a warning, the event is still sent.
 */
internal object MediaEventValidator {

    const val MEDIA_ID_MAX = 36
    const val VARIANT_MAX = 64
    const val AD_ID_MAX = 36
    const val AD_SLOT_MAX = 128
    const val ERROR_CODE_MAX = 8
    const val ERROR_MESSAGE_MAX = 1024
    const val PLAYBACK_RATE_MAX = 16.0

    private const val LOG_PREFIX = "[MediaEvent]"

    fun checkText(method: String, name: String, value: String?, min: Int, max: Int) {
        if (value == null) return
        if (value.length !in min..max) warn(method, "'$name' must have $min to $max characters, got ${value.length}")
    }

    fun checkNotNegative(method: String, name: String, value: Double?) {
        if (value == null) return
        if (!(value >= 0.0)) warn(method, "'$name' must not be negative, got $value")
    }

    fun checkRange(method: String, name: String, value: Double?, min: Double, max: Double) {
        if (value == null) return
        if (!(value in min..max)) warn(method, "'$name' must be in range $min to $max, got $value")
    }

    fun warn(method: String, message: String) = Logger.warn("$LOG_PREFIX $method: $message")

    fun ignored(method: String, reason: String) = Logger.debug("$LOG_PREFIX $method ignored: $reason")
}
