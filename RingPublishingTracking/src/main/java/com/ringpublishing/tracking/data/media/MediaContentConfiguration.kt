package com.ringpublishing.tracking.data.media

/**
 * Configuration of a [MediaContent] passed to [MediaSession.createMediaContent]
 *
 * Every value is optional. 'muted', 'visibilityState', 'playbackRate' and 'audioOutput' replace the matching
 * state of the session when they are set, the other values stay unchanged.
 *
 * @param [monetizationType]: How the content is monetized, sent with every event of the content
 * @param [subscriptionTier]: Subscription tier of the user, sent with every event of the content
 * @param [muted]: Whether the player is muted
 * @param [visibilityState]: Visibility of the player
 * @param [playbackRate]: Playback speed, 1.0 is the normal speed. Expected range is 0 to 16
 * @param [audioOutput]: Device the sound is played on
 */
data class MediaContentConfiguration @JvmOverloads constructor(
    val monetizationType: MediaMonetizationType? = null,
    val subscriptionTier: MediaSubscriptionTier? = null,
    val muted: Boolean? = null,
    val visibilityState: MediaVisibilityState? = null,
    val playbackRate: Double? = null,
    val audioOutput: MediaAudioOutput? = null
)
