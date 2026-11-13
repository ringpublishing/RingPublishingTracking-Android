package com.ringpublishing.tracking.data.media

import com.ringpublishing.tracking.internal.media.MediaContentState
import com.ringpublishing.tracking.internal.media.MediaEventDetails
import com.ringpublishing.tracking.internal.media.MediaEventType
import com.ringpublishing.tracking.internal.media.MediaEventValidator.AD_ID_MAX
import com.ringpublishing.tracking.internal.media.MediaEventValidator.AD_SLOT_MAX
import com.ringpublishing.tracking.internal.media.MediaEventValidator.ERROR_CODE_MAX
import com.ringpublishing.tracking.internal.media.MediaEventValidator.ERROR_MESSAGE_MAX
import com.ringpublishing.tracking.internal.media.MediaEventValidator.MEDIA_ID_MAX
import com.ringpublishing.tracking.internal.media.MediaEventValidator.VARIANT_MAX
import com.ringpublishing.tracking.internal.media.MediaEventValidator.checkNotNegative
import com.ringpublishing.tracking.internal.media.MediaEventValidator.checkText
import com.ringpublishing.tracking.internal.media.MediaEventValidator.ignored
import com.ringpublishing.tracking.internal.media.MediaEventValidator.warn

/**
 * One content played by a [MediaSession], for example a video, an audio episode, an advertisement or a text to speech
 * reading.
 * Create it with [MediaSession.createMediaContent].
 *
 * Report what happens in the player with the methods of this class. A call that is not allowed in the current state of
 * the content sends nothing and changes nothing. 'userEngaged', when passed, replaces the value carried by the content
 * from then on. Positions are in seconds and should not be negative, a negative value is still reported with a warning.
 * Every method can be called from any thread.
 *
 * @see MediaSession
 */
@Suppress("TooManyFunctions")
class MediaContent internal constructor(
    private val session: MediaSession,
    internal val state: MediaContentState
) {

    private enum class Mode { INITIALIZED, PLAYING, PAUSED, ENDED }

    private class HeldResume(val userEngaged: Boolean?)

    private var mode = Mode.INITIALIZED
    private var seeking = false
    private var seekTarget: Double? = null
    private var interruptedContent: MediaContent? = null
    private var heldResume: HeldResume? = null
    private var lastPosition = 0.0

    internal val mediaId: String? get() = state.mediaId
    internal val isActive: Boolean get() = isIn(Mode.PLAYING, Mode.PAUSED)

    /**
     * True when the content is playing or paused and the session is active
     */
    val hasActiveContent: Boolean get() = synchronized(session.lock) { isActive }

    /**
     * True between 'seekStart' and 'seekEnd'
     */
    val isSeeking: Boolean get() = synchronized(session.lock) { seeking }

    /**
     * Reports 'MEDIA_PLAYING_START'. Allowed before the first start and after [playingEnd].
     *
     * An advertisement ([MediaContentType.AD]) pauses the content it belongs to, when that content is playing.
     *
     * @param [parameters]: Description of the started content
     *
     * @see MediaPlayingStartParameters
     */
    fun playingStart(parameters: MediaPlayingStartParameters) = synchronized(session.lock) {
        if (!isIn(Mode.INITIALIZED)) return@synchronized ignored("playingStart")

        val isAd = parameters.mediaContentType == MediaContentType.AD
        val relatedMediaId = if (isAd) parameters.relatedMediaId ?: session.activeContent?.mediaId else null
        validatePlayingStart(parameters, relatedMediaId, isAd)

        mode = Mode.PLAYING
        applyPlayingStart(parameters, relatedMediaId)

        val relatedContent = relatedMediaId?.takeIf { isAd }?.let { session.findActiveContent(it, this) }
        if (relatedContent?.pauseForAd() == true) interruptedContent = relatedContent

        session.addContent(this)
        session.activeContent = this

        val details = MediaEventDetails(positionSec = parameters.positionSec, startMode = parameters.startMode)
        send(MediaEventType.PLAYING_START, details)
    }

    /**
     * Reports 'MEDIA_PLAYING_PROGRESS'. Allowed while playing.
     *
     * @param [positionSec]: Current position
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun playingProgress(positionSec: Double, userEngaged: Boolean? = null) = synchronized(session.lock) {
        if (!isIn(Mode.PLAYING)) return@synchronized ignored("playingProgress")

        checkNotNegative("playingProgress", "positionSec", positionSec)
        state.userEngaged = userEngaged ?: state.userEngaged
        send(MediaEventType.PLAYING_PROGRESS, MediaEventDetails(positionSec = positionSec))
    }

    /**
     * Reports 'MEDIA_PLAYING_END'. Allowed while playing or paused. The same content can be started again afterwards.
     *
     * An open seek is closed first with 'MEDIA_SEEK_END'. When the content interrupted by this advertisement is paused,
     * it is resumed.
     *
     * @param [positionSec]: Position the playback ended at
     * @param [reason]: Why the playback ended
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun playingEnd(
        positionSec: Double,
        reason: MediaPlaybackEndReason,
        userEngaged: Boolean? = null
    ) = synchronized(session.lock) {
        if (!isIn(Mode.PLAYING, Mode.PAUSED)) return@synchronized ignored("playingEnd")

        checkNotNegative("playingEnd", "positionSec", positionSec)
        closePendingSeek()

        mode = Mode.INITIALIZED
        state.userEngaged = userEngaged ?: state.userEngaged
        send(MediaEventType.PLAYING_END, MediaEventDetails(positionSec = positionSec, playbackEndReason = reason))

        session.removeContent(this)
        if (session.activeContent === this) {
            session.activeContent = interruptedContent?.takeIf { it.isActive }
            session.activeContent?.resumeAfterAd()
        }
        interruptedContent = null
    }

    /**
     * Reports 'MEDIA_PAUSE'. Allowed while playing, it is sent at once also during a seek.
     * A resume held by an open seek is dropped in any state.
     *
     * @param [positionSec]: Current position
     * @param [reason]: Why the playback was paused
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun pause(
        positionSec: Double,
        reason: MediaPauseReason,
        userEngaged: Boolean? = null
    ) = synchronized(session.lock) {
        heldResume = null
        if (!isIn(Mode.PLAYING)) return@synchronized ignored("pause")

        checkNotNegative("pause", "positionSec", positionSec)
        emitPause(positionSec, reason, userEngaged)
        Unit
    }

    /**
     * Reports 'MEDIA_RESUME'. Allowed while paused. During a seek the resume is held and reported right after
     * 'MEDIA_SEEK_END', at the position of the seek end.
     *
     * @param [positionSec]: Current position
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun resume(positionSec: Double, userEngaged: Boolean? = null) = synchronized(session.lock) {
        if (!isIn(Mode.PAUSED)) return@synchronized ignored("resume")

        checkNotNegative("resume", "positionSec", positionSec)
        if (seeking) {
            heldResume = HeldResume(userEngaged)
        } else {
            emitResume(positionSec, userEngaged)
        }
    }

    /**
     * Reports 'MEDIA_SEEK_START'. Allowed while playing or paused, when no seek is open.
     *
     * @param [positionSec]: Position the seek starts from
     * @param [targetSec]: Position the seek goes to. When the seek is closed without 'seekEnd', it is reported as the
     * end position
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun seekStart(
        positionSec: Double,
        targetSec: Double? = null,
        userEngaged: Boolean? = null
    ) = synchronized(session.lock) {
        if (!isIn(Mode.PLAYING, Mode.PAUSED) || seeking) return@synchronized ignored("seekStart")

        checkNotNegative("seekStart", "positionSec", positionSec)
        checkNotNegative("seekStart", "targetSec", targetSec)
        seekTarget = targetSec
        seeking = true
        state.userEngaged = userEngaged ?: state.userEngaged
        send(MediaEventType.SEEK_START, MediaEventDetails(positionSec = positionSec))
    }

    /**
     * Reports 'MEDIA_SEEK_END'. Allowed while playing or paused, also without 'seekStart'. A resume held by the seek
     * is reported right after, at the same position.
     *
     * @param [positionSec]: Position the seek ended at
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun seekEnd(positionSec: Double, userEngaged: Boolean? = null) = synchronized(session.lock) {
        if (!isIn(Mode.PLAYING, Mode.PAUSED)) return@synchronized ignored("seekEnd")

        checkNotNegative("seekEnd", "positionSec", positionSec)
        endSeek(positionSec, userEngaged)
    }

    /**
     * Reports 'MEDIA_MUTE' and mutes the session. Allowed while playing or paused. It is sent even when the player
     * was already muted. 'userEngaged' is 'false' unless passed.
     *
     * @param [positionSec]: Current position
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun mute(positionSec: Double, userEngaged: Boolean? = null) = setMuted(true, "mute", positionSec, userEngaged)

    /**
     * Reports 'MEDIA_UNMUTE' and unmutes the session. Allowed while playing or paused. It is sent even when the player
     * was not muted. 'userEngaged' is 'true' unless passed.
     *
     * @param [positionSec]: Current position
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun unmute(positionSec: Double, userEngaged: Boolean? = null) = setMuted(false, "unmute", positionSec, userEngaged)

    /**
     * Reports 'MEDIA_OUTPUT_CHANGE' and changes the audio output of the session. Allowed while playing or paused.
     *
     * @param [positionSec]: Current position
     * @param [audioOutput]: New audio output
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun outputChange(
        positionSec: Double,
        audioOutput: MediaAudioOutput,
        userEngaged: Boolean? = null
    ) = synchronized(session.lock) {
        if (!isIn(Mode.PLAYING, Mode.PAUSED)) return@synchronized ignored("outputChange")

        checkNotNegative("outputChange", "positionSec", positionSec)
        session.sessionState.audioOutput = audioOutput
        state.userEngaged = userEngaged ?: state.userEngaged
        send(MediaEventType.OUTPUT_CHANGE, MediaEventDetails(positionSec = positionSec))
    }

    /**
     * Reports 'MEDIA_VISIBILITY_CHANGE' and changes the visibility of the session. Allowed while playing or paused.
     *
     * @param [positionSec]: Current position
     * @param [visibilityState]: New visibility of the player
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun visibilityChange(
        positionSec: Double,
        visibilityState: MediaVisibilityState,
        userEngaged: Boolean? = null
    ) = synchronized(session.lock) {
        if (!isIn(Mode.PLAYING, Mode.PAUSED)) return@synchronized ignored("visibilityChange")

        checkNotNegative("visibilityChange", "positionSec", positionSec)
        session.sessionState.visibilityState = visibilityState
        state.userEngaged = userEngaged ?: state.userEngaged
        send(MediaEventType.VISIBILITY_CHANGE, MediaEventDetails(positionSec = positionSec))
    }

    /**
     * Reports 'MEDIA_ERROR'. Allowed before the start, while playing and while paused. The state of the content does
     * not change.
     *
     * @param [positionSec]: Current position
     * @param [code]: Error code, 1 to 8 characters
     * @param [message]: Error message, 1 to 1024 characters
     * @param [userEngaged]: Whether the user is engaged
     */
    @JvmOverloads
    fun error(
        positionSec: Double,
        code: String,
        message: String,
        userEngaged: Boolean? = null
    ) = synchronized(session.lock) {
        if (!isIn(Mode.INITIALIZED, Mode.PLAYING, Mode.PAUSED)) return@synchronized ignored("error")

        checkNotNegative("error", "positionSec", positionSec)
        checkText("error", "code", code, 1, ERROR_CODE_MAX)
        checkText("error", "message", message, 1, ERROR_MESSAGE_MAX)
        state.userEngaged = userEngaged ?: state.userEngaged
        val details = MediaEventDetails(positionSec = positionSec, errorCode = code, errorMessage = message)
        send(MediaEventType.ERROR, details)
    }

    // Callers of the internal members below hold the lock of the session.

    internal fun closePendingSeek() {
        heldResume = null
        if (seeking) endSeek(null, null)
    }

    internal fun markEnded() {
        mode = Mode.ENDED
    }

    internal fun pauseForAd(): Boolean = emitPause(contentPosition(), MediaPauseReason.AD, false)

    internal fun resumeAfterAd() = emitResume(contentPosition(), null)

    private fun isIn(vararg modes: Mode) = !session.isEnded && mode in modes

    private fun contentPosition() = seekTarget ?: lastPosition

    private fun ignored(method: String) = ignored(method, "not allowed in state $mode")

    private fun send(eventType: MediaEventType, details: MediaEventDetails) {
        details.positionSec?.let { lastPosition = it }
        session.send(state, eventType, details)
    }

    private fun emitPause(positionSec: Double, reason: MediaPauseReason, userEngaged: Boolean?): Boolean {
        if (!isIn(Mode.PLAYING)) return false

        mode = Mode.PAUSED
        state.userEngaged = userEngaged ?: state.userEngaged
        send(MediaEventType.PAUSE, MediaEventDetails(positionSec = positionSec, pauseReason = reason))
        return true
    }

    private fun emitResume(positionSec: Double, userEngaged: Boolean?) {
        if (!isIn(Mode.PAUSED)) return

        mode = Mode.PLAYING
        session.activeContent = this
        state.userEngaged = userEngaged ?: state.userEngaged
        send(MediaEventType.RESUME, MediaEventDetails(positionSec = positionSec))
    }

    private fun endSeek(positionSec: Double?, userEngaged: Boolean?) {
        seeking = false
        val position = positionSec ?: contentPosition()
        seekTarget = null

        if (isIn(Mode.PLAYING, Mode.PAUSED)) {
            state.userEngaged = userEngaged ?: state.userEngaged
            send(MediaEventType.SEEK_END, MediaEventDetails(positionSec = position))
        }

        val held = heldResume ?: return
        heldResume = null
        emitResume(position, held.userEngaged)
    }

    private fun setMuted(
        muted: Boolean,
        method: String,
        positionSec: Double,
        userEngaged: Boolean?
    ) = synchronized(session.lock) {
        if (!isIn(Mode.PLAYING, Mode.PAUSED)) return@synchronized ignored(method)

        checkNotNegative(method, "positionSec", positionSec)
        session.sessionState.muted = muted
        state.userEngaged = userEngaged ?: !muted
        send(if (muted) MediaEventType.MUTE else MediaEventType.UNMUTE, MediaEventDetails(positionSec = positionSec))
    }

    private fun validatePlayingStart(parameters: MediaPlayingStartParameters, relatedMediaId: String?, isAd: Boolean) {
        val method = "playingStart"
        checkText(method, "mediaId", parameters.mediaId, 1, MEDIA_ID_MAX)
        checkText(method, "mediaVariant", parameters.mediaVariant, 0, VARIANT_MAX)
        checkText(method, "adId", parameters.adId, 0, AD_ID_MAX)
        checkText(method, "adCampaignId", parameters.adCampaignId, 0, AD_ID_MAX)
        checkText(method, "adCreativeId", parameters.adCreativeId, 0, AD_ID_MAX)
        checkText(method, "adSlot", parameters.adSlot, 0, AD_SLOT_MAX)
        checkText(method, "relatedMediaId", relatedMediaId, 0, AD_ID_MAX)
        checkNotNegative(method, "positionSec", parameters.positionSec)
        checkNotNegative(method, "durationSec", parameters.durationSec)

        if (isAd && parameters.mediaContentSubtype == null) {
            warn(method, "'mediaContentSubtype' is expected for an ad")
        }
        if (isAd && relatedMediaId == null) warn(method, "'relatedMediaId' is expected for an ad")
    }

    private fun applyPlayingStart(parameters: MediaPlayingStartParameters, relatedMediaId: String?) {
        state.durationSec = parameters.durationSec
        state.mediaContentType = parameters.mediaContentType
        state.mediaId = parameters.mediaId
        state.playbackIntent = parameters.playbackIntent
        state.adId = parameters.adId
        state.adCampaignId = parameters.adCampaignId
        state.adCreativeId = parameters.adCreativeId
        state.adSlot = parameters.adSlot
        state.mediaContentSubtype = parameters.mediaContentSubtype
        state.mediaPremium = parameters.mediaPremium
        state.mediaTier = parameters.mediaTier
        state.mediaVariant = parameters.mediaVariant
        state.relatedMediaId = relatedMediaId
        parameters.muted?.let { session.sessionState.muted = it }
        state.userEngaged = parameters.userEngaged ?: state.userEngaged
    }
}
