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
        val length = value.codePointCount(0, value.length)
        if (length !in min..max) warn(method, "'$name' must have $min to $max characters, got $length")
    }

    fun checkNotNegative(method: String, name: String, value: Double?) {
        if (value == null || !value.isFinite()) return
        if (!(value >= 0.0)) warn(method, "'$name' must not be negative, got $value")
    }

    fun checkRange(method: String, name: String, value: Double?, min: Double, max: Double) {
        if (value == null || !value.isFinite()) return
        if (!(value in min..max)) warn(method, "'$name' must be in range $min to $max, got $value")
    }

    /**
     * Returns [value], or null with a warning when it is NaN or infinite. JSON cannot carry such a number.
     */
    fun finiteOrNull(method: String, name: String, value: Double?): Double? {
        if (value == null) return null
        if (!value.isFinite()) {
            warn(method, "'$name' must be a finite number, got $value")
            return null
        }
        return value
    }

    /**
     * Position to report: null when [value] is not finite, a negative finite value is reported with a warning.
     */
    fun position(method: String, value: Double): Double? {
        val finite = finiteOrNull(method, "positionSec", value)
        checkNotNegative(method, "positionSec", finite)
        return finite
    }

    fun warn(method: String, message: String) = Logger.warn("$LOG_PREFIX $method: $message")

    fun ignored(method: String, reason: String) = Logger.debug("$LOG_PREFIX $method ignored: $reason")
}
