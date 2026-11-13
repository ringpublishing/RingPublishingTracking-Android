package com.ringpublishing.tracking.data.media

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.factory.MediaEventsFactory
import com.ringpublishing.tracking.internal.media.MediaContentState
import com.ringpublishing.tracking.internal.media.MediaEventDetails
import com.ringpublishing.tracking.internal.media.MediaEventType
import com.ringpublishing.tracking.internal.media.MediaEventValidator.PLAYBACK_RATE_MAX
import com.ringpublishing.tracking.internal.media.MediaEventValidator.VARIANT_MAX
import com.ringpublishing.tracking.internal.media.MediaEventValidator.checkRange
import com.ringpublishing.tracking.internal.media.MediaEventValidator.checkText
import com.ringpublishing.tracking.internal.media.MediaEventValidator.ignored
import com.ringpublishing.tracking.internal.media.MediaSessionState
import java.util.UUID

/**
 * Session of a player that reports MediaEvent. One session describes one player instance (video, audio or text to
 * speech), which can play many contents one after another.
 *
 * Create it with 'RingPublishingTracking.createMediaSession', then create a [MediaContent] for every played content.
 * The session owns 'media_session_id', 'event_ordinal' and the state carried by every event, so the host application
 * only reports what happens in the player.
 *
 * A call that is not allowed in the current state of the session or content sends nothing and changes nothing.
 * Every method can be called from any thread. Events are created under a session lock, which fixes their order, and
 * delivered to the sink after the lock is released, in that order. When another thread is already delivering, the call
 * returns without waiting for it.
 *
 * @see MediaContent
 */
class MediaSession internal constructor(
    mediaType: MediaType,
    playerVariant: String?,
    private val eventSink: (Event) -> Unit,
    private val eventsFactory: MediaEventsFactory = MediaEventsFactory()
) {

    internal val lock = Any()

    private val outbox = ArrayDeque<Event>()
    private var delivering = false

    private val state = MediaSessionState(UUID.randomUUID().toString(), mediaType, playerVariant)
    private var started = false
    private var ended = false
    private var lastPlayback: MediaContentState? = null
    private val contents = LinkedHashSet<MediaContent>()

    internal var activeContent: MediaContent? = null
    internal val isEnded: Boolean get() = ended

    init {
        checkText("createMediaSession", "playerVariant", playerVariant, 0, VARIANT_MAX)
    }

    /**
     * Identifier of the session, UUID v4, sent as 'media_session_id' with every event
     */
    val sessionId: String get() = state.sessionId

    /**
     * False after [end]
     */
    val isActive: Boolean get() = synchronized(lock) { !ended }

    /**
     * Ordinal of the last sent event, 0 before the first one
     */
    val eventOrdinal: Int get() = synchronized(lock) { state.eventOrdinal }

    /**
     * Whether the player is muted
     */
    val muted: Boolean get() = synchronized(lock) { state.muted }

    /**
     * Visibility of the player
     */
    val visibilityState: MediaVisibilityState get() = synchronized(lock) { state.visibilityState }

    /**
     * Device the sound is played on
     */
    val audioOutput: MediaAudioOutput get() = synchronized(lock) { state.audioOutput }

    /**
     * Playback speed, 1.0 is the normal speed
     */
    val playbackRate: Double get() = synchronized(lock) { state.playbackRate }

    /**
     * Creates content of this session
     *
     * The state values set in [configuration] replace the matching state of the session. The first content of a session
     * reports 'MEDIA_SESSION_START'. On an ended session the content is created, but every call on it is ignored.
     *
     * @param [configuration]: Configuration of the content
     * @return New content
     *
     * @see MediaContentConfiguration
     */
    @JvmOverloads
    fun createMediaContent(
        configuration: MediaContentConfiguration = MediaContentConfiguration()
    ): MediaContent = locked {
        checkRange("createMediaContent", "playbackRate", configuration.playbackRate, 0.0, PLAYBACK_RATE_MAX)

        configuration.audioOutput?.let { state.audioOutput = it }
        configuration.muted?.let { state.muted = it }
        configuration.playbackRate?.let { state.playbackRate = it }
        configuration.visibilityState?.let { state.visibilityState = it }

        val contentState = MediaContentState(configuration.monetizationType, configuration.subscriptionTier)
        val content = MediaContent(this, contentState)
        start(content)
        content
    }

    /**
     * Changes the playback speed carried by the next events. Sends nothing.
     *
     * @param [playbackRate]: Playback speed, 1.0 is the normal speed. Expected range is 0 to 16
     */
    fun updatePlaybackRate(playbackRate: Double) = locked {
        if (ended) {
            ignored("updatePlaybackRate", "session has ended")
            return@locked
        }

        checkRange("updatePlaybackRate", "playbackRate", playbackRate, 0.0, PLAYBACK_RATE_MAX)
        state.playbackRate = playbackRate
    }

    /**
     * Ends the session. Reports 'MEDIA_SESSION_END' when the session has started. Every content of the session becomes
     * inert. Calling it again does nothing.
     */
    fun end() = locked {
        if (ended) {
            ignored("end", "session has ended")
            return@locked
        }

        contents.toList().forEach { it.closePendingSeek() }

        ended = true
        activeContent = null
        contents.forEach { it.markEnded() }
        contents.clear()

        if (started) send(lastPlayback, MediaEventType.SESSION_END, MediaEventDetails())
    }

    internal val sessionState: MediaSessionState get() = state

    internal fun addContent(content: MediaContent) {
        contents.add(content)
    }

    internal fun removeContent(content: MediaContent) {
        contents.remove(content)
    }

    internal fun findActiveContent(mediaId: String, except: MediaContent): MediaContent? =
        contents.firstOrNull { it !== except && it.isActive && it.mediaId == mediaId }

    internal fun send(playback: MediaContentState?, eventType: MediaEventType, details: MediaEventDetails) {
        lastPlayback = playback ?: lastPlayback
        state.eventOrdinal += 1
        outbox.addLast(eventsFactory.createMediaEvent(eventType, state, playback, details))
    }

    /**
     * Runs [block] under the lock, then delivers the events it queued. No SDK or host code runs under the lock.
     */
    internal fun <T> locked(block: () -> T): T {
        val result = synchronized(lock, block)
        deliverPending()
        return result
    }

    /**
     * Only one thread delivers at a time. A call made meanwhile, also from inside the sink, leaves its events in the
     * outbox for the running loop.
     */
    private fun deliverPending() {
        synchronized(lock) {
            if (delivering || outbox.isEmpty()) return
            delivering = true
        }

        while (true) {
            val batch = synchronized(lock) {
                if (outbox.isEmpty()) {
                    delivering = false
                    return
                }
                outbox.toList().also { outbox.clear() }
            }

            for ((index, event) in batch.withIndex()) {
                try {
                    eventSink(event)
                } catch (error: Throwable) {
                    synchronized(lock) {
                        batch.drop(index + 1).asReversed().forEach { outbox.addFirst(it) }
                        delivering = false
                    }
                    throw error
                }
            }
        }
    }

    private fun start(content: MediaContent) {
        if (started || ended) return

        started = true
        send(content.state, MediaEventType.SESSION_START, MediaEventDetails())
    }
}
