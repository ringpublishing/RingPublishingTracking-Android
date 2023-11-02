package com.ringpublishing.tracking.internal.video

internal enum class VideoEventParam(val text: String) {
    SELECTED_ELEMENT_NAME("VE"),
    EVENT_TYPE("RT"),
    CONTENT_ID("PMU"),
    IS_MUTED("MUTED"),
    LENGTH("VT"),
    CURRENT_TIME("VP"),
    DATA("VC"),
    TIMESTAMP("RR"),
    COUNTER("VEN"),
    FORMAT("VS"),
    START_MODE("VSM"),
    PLAYER_VERSION("XI"),
    IS_MAIN_CONTENT_PIECE("VSLOT"),
    IS_MAIN_VIDEO("VEM"),
    CONTENT_CATEGORY("VPC"),
    VISIBILITY("ECX"),
    ADS_CONFIGURATION("VNA")
}
