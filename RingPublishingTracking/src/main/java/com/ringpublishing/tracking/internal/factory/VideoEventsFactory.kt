package com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.Gson
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.data.video.VideoEvent
import com.ringpublishing.tracking.data.video.VideoMetadata
import com.ringpublishing.tracking.data.video.VideoState
import com.ringpublishing.tracking.data.video.VideoVisibility
import com.ringpublishing.tracking.data.video.VideoVisibilityContext
import com.ringpublishing.tracking.data.video.VideoVisibilityState
import com.ringpublishing.tracking.internal.video.VideoEventParam
import kotlin.math.roundToInt

internal class VideoEventsFactory(private val gson: Gson) {

    private val sessionTimestamps: MutableMap<String, String> = mutableMapOf()
    private val sessionCounters: MutableMap<String, Int> = mutableMapOf()

    fun createVideoEvent(videoEvent: VideoEvent, videoMetadata: VideoMetadata, videoState: VideoState): Event {

        val parameters = mutableMapOf<String, Any>().apply {
            this[VideoEventParam.SELECTED_ELEMENT_NAME.text] = videoEvent.text
            this[VideoEventParam.EVENT_TYPE.text] = EventType.VIDEO.text
            this[VideoEventParam.CONTENT_ID.text] = videoMetadata.contentId
            this[VideoEventParam.IS_MUTED.text] = if (videoState.isMuted) 1 else 0
            this[VideoEventParam.LENGTH.text] = videoMetadata.videoDuration
            this[VideoEventParam.CURRENT_TIME.text] = videoState.currentTime
            this[VideoEventParam.DATA.text] = createVideoEventVCParameter(videoMetadata, videoState)
            this[VideoEventParam.TIMESTAMP.text] = getAndUpdateSessionTimestamp(videoMetadata.contentId, videoEvent)
            this[VideoEventParam.COUNTER.text] = getAndUpdateSessionCounter(videoMetadata.contentId, videoEvent)
            this[VideoEventParam.FORMAT.text] = videoMetadata.videoStreamFormat.text
            this[VideoEventParam.START_MODE.text] = videoState.startMode.text
            this[VideoEventParam.PLAYER_VERSION.text] = videoMetadata.videoPlayerVersion
            this[VideoEventParam.IS_MAIN_CONTENT_PIECE.text] = if (videoMetadata.isMainContentPiece) "player" else "splayer"
            this[VideoEventParam.IS_MAIN_VIDEO.text] = if (videoMetadata.isMainContentPiece) "mainVideo" else "inTextVideo"
            this[VideoEventParam.CONTENT_CATEGORY.text] = videoMetadata.videoContentCategory.text
            this[VideoEventParam.VISIBILITY.text] = createVisibilityParam(videoState.visibilityState)

            videoMetadata.videoAdsConfiguration.text?.let {
                this[VideoEventParam.ADS_CONFIGURATION.text] = it
            }
        }

        return Event(
            analyticsSystemName = AnalyticsSystem.KROPKA_STATS.text,
            name = videoEvent.text,
            parameters = parameters
        )
    }

    /**
     * Creates VC video event parameter
     * Format: prefix:ckmId,publicationId,video/type,bitrate
     * Example: "video:2334518,2334518.275928614,video/hls,4000"
     */
    fun createVideoEventVCParameter(metadata: VideoMetadata, videoState: VideoState): String {
        val ckmId = metadata.publicationId
            .split(".")[0]
        val bitrate = videoState
            .currentBitrate
            .toFloat()
            .roundToInt()

        return "video:$ckmId,${metadata.publicationId},video/${metadata.videoStreamFormat.text},$bitrate"
    }

    /**
     * Retrieves timestamp string from sessionTimestamps for given [contentId].
     * Creates one and puts in map if it does not exist or if [videoEvent] value == [VideoEvent.START]
     */
    fun getAndUpdateSessionTimestamp(contentId: String, videoEvent: VideoEvent) =
        sessionTimestamps[contentId].let { savedTimestamp ->
            if (savedTimestamp == null || videoEvent == VideoEvent.START) {
                "${System.currentTimeMillis()}".also { sessionTimestamps[contentId] = it }
            } else {
                savedTimestamp
            }
        }

    /**
     * Retrieves and increment by 1 video session counter for given [contentId].
     * If counter is not found or [videoEvent] value == [VideoEvent.START] new counter is saved with value 0.
     */
    fun getAndUpdateSessionCounter(contentId: String, videoEvent: VideoEvent) =
        sessionCounters[contentId].let { savedCounter ->
            if (savedCounter == null || videoEvent == VideoEvent.START) 0
            else savedCounter + 1
        }.also {
            sessionCounters[contentId] = it
        }

    /**
     * Creates json string based on [VideoVisibility] class structure, containing given [VideoVisibilityState] value and encodes this json with Base64.
     * example json to encode: {"context":{"visible":"out-of-viewport"}}
     */
    fun createVisibilityParam(visibility: VideoVisibilityState): String =
        gson.toJson(VideoVisibility(VideoVisibilityContext(visibility.text))).let { json ->
            Base64.encodeToString(json.toByteArray(), Base64.DEFAULT)
        }
}
