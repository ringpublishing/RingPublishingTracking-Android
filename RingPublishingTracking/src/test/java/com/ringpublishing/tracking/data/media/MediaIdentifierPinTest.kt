package com.ringpublishing.tracking.data.media

import com.google.gson.Gson
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.decorator.EventDecorator
import com.ringpublishing.tracking.internal.decorator.SessionIdentifierDecorator
import com.ringpublishing.tracking.internal.mockAndroidBase64Encoding
import com.ringpublishing.tracking.internal.repository.ApiRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Runs media events through the real EventDecorator, so the IP and IV are the ones that go out.
 */
class MediaIdentifierPinTest {

    private val configuration = ConfigurationManager()
    private lateinit var decorator: EventDecorator
    private val sent = mutableListOf<Event>()

    @Before
    fun before() {
        mockAndroidBase64Encoding()
        configuration.initializeConfiguration(mockk<RingPublishingTrackingConfiguration>(relaxed = true))
        decorator = EventDecorator(
            configuration,
            mockk<ApiRepository>(relaxed = true).also { every { it.readArtemisId() } returns null },
            Gson(),
            mockk(relaxed = true),
            mockk(relaxed = true),
            SessionIdentifierDecorator()
        )
    }

    @After
    fun after() {
        unmockkAll()
    }

    private fun session(): MediaSession = MediaSession(MediaType.AUDIO, null, { event ->
        decorator.decorate(event)
        synchronized(sent) { sent.add(event) }
    })

    private fun report(event: Event): Event = decorator.decorate(event).also { synchronized(sent) { sent.add(it) } }

    private fun pageView(partiallyReloaded: Boolean = false): Event {
        configuration.updatePartiallyReloaded(partiallyReloaded)
        return report(Event(name = "PageView"))
    }

    private fun start(mediaId: String = "m1", type: MediaContentType = MediaContentType.MAIN, related: String? = null) =
        MediaPlayingStartParameters(
            mediaId = mediaId,
            durationSec = 100.0,
            mediaContentType = type,
            playbackIntent = MediaPlaybackIntent.USER_INITIATED,
            positionSec = 0.0,
            mediaContentSubtype = if (type == MediaContentType.AD) MediaContentSubtype.PREROLL else null,
            relatedMediaId = related
        )

    private fun Event.ip() = parameters["IP"]
    private fun Event.iv() = parameters["IV"]
    private fun Event.type() = parameters["event_type"]
    private fun types() = sent.map { it.type() ?: it.name }
    private fun at(type: String) = sent.first { it.type() == type }

    private fun assertIds(ip: Any?, iv: Any?, events: List<Event>) = events.forEach {
        assertEquals("IP of ${it.type()}", ip, it.ip())
        assertEquals("IV of ${it.type()}", iv, it.iv())
    }

    @Test
    fun eventsAfterPageView_KeepIdentifiersOfPlayingStart() {
        val screenA = pageView()
        val session = session()
        val content = session.createMediaContent()
        content.playingStart(start())
        val startEvent = sent.last()
        assertEquals(screenA.ip(), startEvent.ip())

        val screenB = pageView()
        assertNotEquals(screenA.ip(), screenB.ip())
        val firstOfItem = sent.size

        content.playingProgress(5.0)
        content.pause(6.0, MediaPauseReason.USER_ACTION)
        content.seekStart(6.0, targetSec = 50.0)
        content.resume(6.0)
        content.seekEnd(50.0)
        content.mute(51.0)
        content.unmute(52.0)
        content.outputChange(53.0, MediaAudioOutput.CAR)
        content.visibilityChange(54.0, MediaVisibilityState.BACKGROUND)
        content.error(55.0, "E1", "failure")
        content.playingEnd(56.0, MediaPlaybackEndReason.USER_STOPPED)

        val item = sent.drop(firstOfItem)
        assertEquals(
            listOf(
                "MEDIA_PLAYING_PROGRESS", "MEDIA_PAUSE", "MEDIA_SEEK_START", "MEDIA_SEEK_END", "MEDIA_RESUME",
                "MEDIA_MUTE", "MEDIA_UNMUTE", "MEDIA_OUTPUT_CHANGE", "MEDIA_VISIBILITY_CHANGE", "MEDIA_ERROR",
                "MEDIA_PLAYING_END"
            ),
            item.map { it.type() }
        )
        assertIds(screenA.ip(), screenA.iv(), item)

        val afterPageView = report(Event(name = "Other"))
        assertEquals(screenB.ip(), screenB.parameters["IP"])
        assertEquals(screenB.ip(), afterPageView.ip())
        assertEquals(screenB.iv(), afterPageView.iv())
    }

    @Test
    fun partialReload_DoesNotChangePinnedIdentifiers() {
        val screenA = pageView()
        val content = session().createMediaContent()
        content.playingStart(start())

        val reload = pageView(partiallyReloaded = true)
        assertEquals(screenA.ip(), reload.ip())
        assertNotEquals(screenA.iv(), reload.iv())

        content.playingProgress(5.0)
        content.pause(6.0, MediaPauseReason.USER_ACTION)

        assertIds(screenA.ip(), screenA.iv(), sent.drop(sent.indexOf(reload) + 1))
    }

    @Test
    fun replay_PinsIdentifiersCurrentAtThePlayingStart() {
        val screenA = pageView()
        val content = session().createMediaContent()
        content.playingStart(start())
        content.playingEnd(10.0, MediaPlaybackEndReason.NATURAL_COMPLETION)
        val screenB = pageView()

        content.playingStart(start())
        content.playingProgress(1.0)
        pageView()
        content.playingEnd(2.0, MediaPlaybackEndReason.USER_STOPPED)

        val firstRun = sent.takeWhile { it !== screenB }.filter { it.type() != null }
        assertIds(screenA.ip(), screenA.iv(), firstRun.filter { it.type() != "MEDIA_SESSION_START" })
        val secondRun = sent.drop(sent.indexOf(screenB) + 1).filter { it.type() != null }
        assertEquals(
            listOf("MEDIA_PLAYING_START", "MEDIA_PLAYING_PROGRESS", "MEDIA_PLAYING_END"),
            secondRun.map { it.type() }
        )
        assertIds(screenB.ip(), screenB.iv(), secondRun)
    }

    @Test
    fun adInterruption_MainKeepsItsPinAndAdPinsItsOwn() {
        val screenA = pageView()
        val session = session()
        val main = session.createMediaContent()
        main.playingStart(start("main"))
        val screenB = pageView()

        val ad = session.createMediaContent()
        ad.playingStart(start("ad", MediaContentType.AD, related = "main"))
        ad.playingProgress(1.0)
        ad.playingEnd(2.0, MediaPlaybackEndReason.NATURAL_COMPLETION)

        val adEvents = sent.filter { it.parameters["media_id"] == "ad" }
        assertEquals(3, adEvents.size)
        assertIds(screenB.ip(), screenB.iv(), adEvents)
        val mainPause = sent.first { it.type() == "MEDIA_PAUSE" }
        val mainResume = sent.first { it.type() == "MEDIA_RESUME" }
        assertEquals("main", mainPause.parameters["media_id"])
        assertEquals("main", mainResume.parameters["media_id"])
        assertIds(screenA.ip(), screenA.iv(), listOf(mainPause, mainResume))
    }

    @Test
    fun sessionEnd_CarriesPinOfLastContent_AndSessionStartTheCurrentIdentifiers() {
        val screenA = pageView()
        val session = session()
        val content = session.createMediaContent()
        val sessionStart = sent.last()
        assertEquals("MEDIA_SESSION_START", sessionStart.type())
        assertEquals(screenA.ip(), sessionStart.ip())
        content.playingStart(start())
        pageView()

        session.end()

        assertIds(screenA.ip(), screenA.iv(), listOf(at("MEDIA_SESSION_END")))
    }

    @Test
    fun sessionStartAndContentWithoutPin_CarryCurrentIdentifiers() {
        pageView()
        val session = session()
        val content = session.createMediaContent()
        val screenB = pageView()

        session.end()

        assertEquals(listOf("MEDIA_SESSION_START", "PageView", "MEDIA_SESSION_END"), types().drop(1))
        assertIds(screenB.ip(), screenB.iv(), listOf(at("MEDIA_SESSION_END")))
    }

    @Test
    fun sessions_ArePinnedIndependently() {
        val screenA = pageView()
        val first = session().createMediaContent()
        first.playingStart(start("first"))
        val screenB = pageView()
        val second = session().createMediaContent()
        second.playingStart(start("second"))
        pageView()

        first.playingProgress(1.0)
        second.playingProgress(1.0)

        assertIds(screenA.ip(), screenA.iv(), sent.filter { it.parameters["media_id"] == "first" })
        assertIds(screenB.ip(), screenB.iv(), sent.filter { it.parameters["media_id"] == "second" })
    }

    @Test
    fun nonMediaEvents_AreNotAffected() {
        val screenA = pageView()
        val content = session().createMediaContent()
        content.playingStart(start())
        val screenB = pageView()

        val user = report(Event(name = "UserAction"))

        assertIds(screenB.ip(), screenB.iv(), listOf(user))
        assertNotEquals(screenA.ip(), user.ip())
    }

    @Test
    fun pinnedEvent_IsSerializedWithoutThePin() {
        pageView()
        session().createMediaContent().playingStart(start())

        val json = Gson().toJson(sent.last())

        assertEquals(false, json.contains("dentifierPin") || json.contains("pinsIdentifiers"))
    }

    @Test
    fun concurrentPageViews_DoNotLeakIntoPinnedEvents() {
        val screenA = pageView()
        val content = session().createMediaContent()
        content.playingStart(start())
        val executor = Executors.newFixedThreadPool(4)
        val done = CountDownLatch(4)

        repeat(2) {
            executor.execute {
                repeat(200) { pageView(it % 2 == 0) }
                done.countDown()
            }
        }
        repeat(2) {
            executor.execute {
                repeat(200) { content.playingProgress(it.toDouble()) }
                done.countDown()
            }
        }
        assertEquals(true, done.await(30, TimeUnit.SECONDS))
        executor.shutdownNow()

        val progress = sent.filter { it.type() == "MEDIA_PLAYING_PROGRESS" }
        assertEquals(400, progress.size)
        assertIds(screenA.ip(), screenA.iv(), progress)
    }
}
