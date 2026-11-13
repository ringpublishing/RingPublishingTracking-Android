package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.media.MediaSession
import com.ringpublishing.tracking.data.media.MediaType

/**
 * Creates a session that reports MediaEvent of one player: video, audio or text to speech.
 *
 * Create a [MediaSession] for every player instance, then a content for every played item:
 *
 * ```
 * val session = RingPublishingTracking.createMediaSession(MediaType.AUDIO, playerVariant = "main")
 * val content = session.createMediaContent(MediaContentConfiguration(monetizationType = MediaMonetizationType.FREE))
 * content.playingStart(
 *     MediaPlayingStartParameters("episode-1", 1800.0, MediaContentType.MAIN, MediaPlaybackIntent.USER_INITIATED, 0.0)
 * )
 * content.playingProgress(15.0)
 * content.playingEnd(1800.0, MediaPlaybackEndReason.NATURAL_COMPLETION)
 * session.end()
 * ```
 *
 * The session sends events through the same path as [reportEvent], so an event reported before the module is
 * initialized is dropped with a warning.
 * Every method of the session can be called from any thread.
 *
 * @param [mediaType]: Kind of the player
 * @param [playerVariant]: Variant of the player, up to 64 characters
 * @return New session
 *
 * @see MediaSession
 */
@JvmOverloads
fun RingPublishingTracking.createMediaSession(mediaType: MediaType, playerVariant: String? = null): MediaSession =
    MediaSession(mediaType, playerVariant, { event -> reportEvent(event) }, mediaEventsFactory)
