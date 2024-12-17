package com.ringpublishing.tracking.data.audio

/**
 * Audio media type is used for analytics.
 * @param [text] - type param text
 */
enum class AudioMediaType(val text: String) {
    /**
     * Represents audio content in the format of podcasts, typically longer thematic recordings
     * designed for regular listening in episodes.
     */
    PODCAST("podcast"),

    /**
     * Refers to audio content created or sponsored by brands, often used for marketing,
     * brand awareness, or product promotion purposes.
     */
    BRAND("brand"),

    /**
     * Represents traditional live radio broadcasts or their digital equivalents, offering a wide
     * range of content such as music, news, or thematic shows.
     */
    RADIO("radio"),

    /**
     * Stands for "Text-to-Speech", referring to synthetically generated audio from text,
     * often used in accessibility applications, voice assistants, or automation.
     */
    TTS("tts"),
}
