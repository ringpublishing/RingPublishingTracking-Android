package com.ringpublishing.tracking.data.video

/**
 * Video visibility state related params
 * @param [text] - format param text
 */
enum class VideoVisibilityState(val text: String) {

    /**
     * Video is visible for the user
     */
    VISIBLE("visible"),

    /**
     * Video is not visible (out of viewport) for the user
     */
    OUT_OF_VIEWPORT("out-of-viewport")
}

class VideoVisibilityContext(val visible: String)

class VideoVisibility(val context: VideoVisibilityContext)
