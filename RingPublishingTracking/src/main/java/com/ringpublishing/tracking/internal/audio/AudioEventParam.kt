package com.ringpublishing.tracking.internal.audio

internal enum class AudioEventParam(val text: String) {
    SELECTED_ELEMENT_NAME("VE"),
    EVENT_TYPE("RT"),
    CONTENT_ID("PMU"),
    DURATION("VT"),
    CURRENT_TIME("VP"),
    AUDIO_PARAMETERS("VC"),
    TIMESTAMP("RR"),
    COUNTER("VEN"),
    FORMAT("VS"),
    START_MODE("VSM"),
    AUDIO_PLAYER_VERSION("XI"),
    PLAYER_TYPE("VSLOT"),
    IS_MAIN_AUDIO("VEM"),
    AUDIO_CONTENT_CATEGORY("VPC"),
    CONTEXT("ECX"),
    IS_CONTENT_FRAGMENT("FRA"),
    AUDIO("AUDIO")
}
