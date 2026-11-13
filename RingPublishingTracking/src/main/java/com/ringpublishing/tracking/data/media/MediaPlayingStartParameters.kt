package com.ringpublishing.tracking.data.media

/**
 * Parameters of [MediaContent.playingStart]
 *
 * Values that exceed the allowed length or range are still reported, and a warning is logged.
 *
 * @param [mediaId]: Id of the content, 1 to 36 characters
 * @param [durationSec]: Duration of the content in seconds, not negative
 * @param [mediaContentType]: Role of the content
 * @param [playbackIntent]: What caused the playback to start
 * @param [positionSec]: Position the playback starts from, in seconds, not negative
 * @param [mediaVariant]: Variant of the content, up to 64 characters
 * @param [startMode]: Sound state at the moment the playback started
 * @param [muted]: Whether the player is muted, when set replaces the muted state of the session
 * @param [userEngaged]: Whether the user is engaged, when set replaces the value carried by the content
 * @param [mediaContentSubtype]: Position of an advertisement, expected for [MediaContentType.AD]
 * @param [adId]: Id of the advertisement, up to 36 characters
 * @param [adCampaignId]: Id of the advertisement campaign, up to 36 characters
 * @param [adCreativeId]: Id of the advertisement creative, up to 36 characters
 * @param [adSlot]: Advertisement slot, up to 128 characters
 * @param [relatedMediaId]: Id of the content an advertisement belongs to, up to 36 characters. Used for
 * [MediaContentType.AD] only. When it is not set, the content that is currently active is used
 * @param [mediaPremium]: Whether the content is premium
 * @param [mediaTier]: Tier the content belongs to
 */
data class MediaPlayingStartParameters @JvmOverloads constructor(
    val mediaId: String,
    val durationSec: Double,
    val mediaContentType: MediaContentType,
    val playbackIntent: MediaPlaybackIntent,
    val positionSec: Double,
    val mediaVariant: String? = null,
    val startMode: MediaStartMode? = null,
    val muted: Boolean? = null,
    val userEngaged: Boolean? = null,
    val mediaContentSubtype: MediaContentSubtype? = null,
    val adId: String? = null,
    val adCampaignId: String? = null,
    val adCreativeId: String? = null,
    val adSlot: String? = null,
    val relatedMediaId: String? = null,
    val mediaPremium: Boolean? = null,
    val mediaTier: MediaTier? = null
)
