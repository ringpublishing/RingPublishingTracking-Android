package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.aureus.AureusEventContext
import com.ringpublishing.tracking.data.aureus.AureusTeaser
import com.ringpublishing.tracking.internal.aureus.AureusEventParam
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.factory.AureusEventFactory
import com.ringpublishing.tracking.internal.factory.EventType
import com.ringpublishing.tracking.internal.factory.UserEventParam
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL

class AureusEventsFactoryTest {

    private val snakeCaseGson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    @Before
    fun `Bypass android_util_Base64 to java_util_Base64`() {
        mockkStatic(Base64::class)
        val arraySlot = slot<ByteArray>()

        every {
            Base64.encodeToString(capture(arraySlot), Base64.NO_WRAP)
        } answers {
            java.util.Base64.getEncoder().encodeToString(arraySlot.captured)
        }
    }

    @Test
    fun createAureusImpressionEvent_ThenCorrectResult() {
        val eventsFactory = AureusEventFactory(snakeCaseGson)
        val teasers = listOf(
            AureusTeaser("teaser_id_1", "content_id_1"),
            AureusTeaser("teaser_id_2", "content_id_2")
        )
        val aureusEventContext = AureusEventContext(
            clientUuid = "581ad584-2333-4e69-8963-c105184cfd04",
            variantUuid = "0e8c860f-006a-49ef-923c-38b8cfc7ca57",
            batchId = "79935e2327",
            recommendationId = "e4b25216db",
            segmentId = "group1.segment1"
        )
        val event = eventsFactory.createAureusImpressionEvent(teasers, aureusEventContext)

        Assert.assertEquals(AnalyticsSystem.GENERIC.text, event.analyticsSystemName)
        Assert.assertEquals(EventType.AUREUS_IMPRESSION_EVENT.text, event.name)
        Assert.assertEquals(event.parameters[AureusEventParam.DISPLAYED_ITEMS.text], snakeCaseGson.toJson(teasers))
        Assert.assertEquals(event.parameters[AureusEventParam.CLIENT_UUID.text], aureusEventContext.clientUuid)
        Assert.assertEquals(event.parameters[AureusEventParam.VARIANT_UUID.text], aureusEventContext.variantUuid)
        Assert.assertEquals(event.parameters[AureusEventParam.SEGMENT_ID.text], aureusEventContext.segmentId)
        Assert.assertEquals(event.parameters[AureusEventParam.BATCH_ID.text], aureusEventContext.batchId)
        Assert.assertEquals(event.parameters[AureusEventParam.RECOMMENDATION_ID.text], aureusEventContext.recommendationId)
    }

    @Test
    fun createClickEvent_ThenCorrectResult() {
        val eventsFactory = AureusEventFactory(snakeCaseGson)
        val eventName = "eventName"
        val publicationUrl = "https://domain.com"
        val offerId = "a4gb35"
        val teaser = AureusTeaser("teaser_id_1", "content_id_1")
        val aureusEventContext = AureusEventContext(
            clientUuid = "581ad584-2333-4e69-8963-c105184cfd04",
            variantUuid = "0e8c860f-006a-49ef-923c-38b8cfc7ca57",
            batchId = "79935e2327",
            recommendationId = "e4b25216db",
            segmentId = "group1.segment1"
        )
        val event = eventsFactory.createAureusClickEvent(
            selectedEventName = eventName,
            publicationUrl = URL(publicationUrl),
            aureusOfferId = offerId,
            teaser = teaser,
            eventContext = aureusEventContext
        )

        Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
        Assert.assertEquals(EventType.CLICK.text, event.name)
        Assert.assertEquals(event.parameters[UserEventParam.SELECTED_ELEMENT_NAME.text], eventName)
        Assert.assertEquals(event.parameters[UserEventParam.TARGET_URL.text], publicationUrl)
        Assert.assertEquals(event.parameters[UserEventParam.PAGE_VIEW_RESOURCE_IDENTIFIER.text], teaser.contentId)
        Assert.assertEquals(event.parameters[AureusEventParam.EI.text], offerId)
        Assert.assertEquals(event.parameters[AureusEventParam.ECX.text], prepareEncodedAureusEventContext())
    }

    private fun prepareEncodedAureusEventContext(): String? {
        val aureusEventContextJson =
            "{\"context\":{\"aureus\":{\"client_uuid\":\"581ad584-2333-4e69-8963-c105184cfd04\",\"variant_uuid\":\"0e8c860f-006a-49ef-923c-38b8cfc7ca57\",\"batch_id\":\"79935e2327\",\"recommendation_id\":\"e4b25216db\",\"segment_id\":\"group1.segment1\",\"teaser_id\":\"teaser_id_1\"}}}"
        return runCatching {
            Base64.encodeToString(aureusEventContextJson.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }.getOrNull()
    }
}
