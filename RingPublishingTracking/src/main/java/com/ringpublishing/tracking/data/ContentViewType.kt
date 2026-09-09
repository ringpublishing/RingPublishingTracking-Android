package com.ringpublishing.tracking.data

/**
 * Type of content displayed by a content page view.
 *
 * The associated [value] is the stable value sent in the event's RDLC parameter.
 */
enum class ContentViewType(val value: String) {
    TEXT("text"),
    TTS("tts"),
    SMARTSHORT("smartshort"),
}
