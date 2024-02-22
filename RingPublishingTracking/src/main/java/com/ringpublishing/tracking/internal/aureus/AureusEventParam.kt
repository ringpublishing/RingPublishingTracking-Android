package com.ringpublishing.tracking.internal.aureus

internal enum class AureusEventParam(val text: String)
{
    DISPLAYED_ITEMS("displayed_items"),
    CLIENT_UUID("client_uuid"),
    VARIANT_UUID("variant_uuid"),
    SEGMENT_ID("segment_id"),
    BATCH_ID("batch_id"),
    RECOMMENDATION_ID("recommendation_id"),
    EI("EI"),
    ECX("ECX"),
}