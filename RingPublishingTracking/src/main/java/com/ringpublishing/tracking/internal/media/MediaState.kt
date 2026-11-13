package com.ringpublishing.tracking.internal.media

import com.ringpublishing.tracking.data.media.MediaAudioOutput
import com.ringpublishing.tracking.data.media.MediaContentSubtype
import com.ringpublishing.tracking.data.media.MediaContentType
import com.ringpublishing.tracking.data.media.MediaMonetizationType
import com.ringpublishing.tracking.data.media.MediaPauseReason
import com.ringpublishing.tracking.data.media.MediaPlaybackEndReason
import com.ringpublishing.tracking.data.media.MediaPlaybackIntent
import com.ringpublishing.tracking.data.media.MediaStartMode
import com.ringpublishing.tracking.data.media.MediaSubscriptionTier
import com.ringpublishing.tracking.data.media.MediaTier
import com.ringpublishing.tracking.data.media.MediaType
import com.ringpublishing.tracking.data.media.MediaVisibilityState

/**
 * State shared by every content of a session. Guarded by the lock of the session.
 */
internal class MediaSessionState(
    val sessionId: String,
    val mediaType: MediaType,
    val playerVariant: String?
) {
    var eventOrdinal: Int = 0
    var muted: Boolean = true
    var visibilityState: MediaVisibilityState = MediaVisibilityState.VISIBLE
    var playbackRate: Double = 1.0
    var audioOutput: MediaAudioOutput = MediaAudioOutput.SPEAKER
}

/**
 * State of one content, carried by every event the content sends. Guarded by the lock of the session.
 */
internal class MediaContentState(
    val monetizationType: MediaMonetizationType?,
    val subscriptionTier: MediaSubscriptionTier?
) {
    var userEngaged: Boolean = false
    var durationSec: Double? = null
    var mediaContentType: MediaContentType? = null
    var mediaId: String? = null
    var playbackIntent: MediaPlaybackIntent? = null
    var mediaVariant: String? = null
    var mediaPremium: Boolean? = null
    var mediaTier: MediaTier? = null
    var mediaContentSubtype: MediaContentSubtype? = null
    var adId: String? = null
    var adCampaignId: String? = null
    var adCreativeId: String? = null
    var adSlot: String? = null
    var relatedMediaId: String? = null
}

/**
 * Fields that belong to a single event and are never carried over.
 */
internal data class MediaEventDetails(
    val positionSec: Double? = null,
    val startMode: MediaStartMode? = null,
    val pauseReason: MediaPauseReason? = null,
    val playbackEndReason: MediaPlaybackEndReason? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null
)
