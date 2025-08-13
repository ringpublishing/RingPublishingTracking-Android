/*
 * Created by Daniel Całka on 13/08/24, 1:49 PM
 * Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.factory.com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.ringpublishing.tracking.data.aureus.AureusEventContext
import com.ringpublishing.tracking.data.aureus.AureusTeaser
import com.ringpublishing.tracking.internal.aureus.AureusEventParam
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.factory.AureusEventFactory
import com.ringpublishing.tracking.internal.factory.EventType
import com.ringpublishing.tracking.internal.factory.EventsFactory
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

    private val gson = GsonBuilder().create()

    private val eventsFactory = EventsFactory(gson = gson)

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
    fun createNewAureusImpressionEvent_ThenCorrectResult() {
        val eventsFactory = AureusEventFactory(snakeCaseGson, eventsFactory)

        val teasers = listOf(
            AureusTeaser("teaserId", "offerId", "contentId"),
            AureusTeaser("teaserId_2", "offerId_2", "contentId_2"),
        )

        val aureusEventContext = AureusEventContext(
            variantUuid = "4f37f85f-a8ad-4e6c-a426-5a42fce67ecc",
            batchId = "g9fewcisss",
            recommendationId = "a5uam4ufuu",
            segmentId = "uuid_word2vec_artemis_id_bisect_50_10.8",
            impressionEventType = "AUREUS_IMPRESSION_EVENT_AND_USER_ACTION",
        )

        val event = eventsFactory.createNewAureusImpressionEvent(teasers, aureusEventContext)
        val parameters = event.parameters[AureusEventParam.EVENTS.text] as Map<*, *>

        Assert.assertEquals(AnalyticsSystem.GENERIC.text, event.analyticsSystemName)
        Assert.assertEquals(EventType.AUREUS_IMPRESSION_EVENT.text, event.name)
        Assert.assertEquals(parameters[AureusEventParam.DISPLAYED_ITEMS.text], snakeCaseGson.fromJson(snakeCaseGson.toJson(teasers), JsonArray::class.java))
        Assert.assertEquals(parameters[AureusEventParam.VARIANT_UUID.text], aureusEventContext.variantUuid)
        Assert.assertEquals(parameters[AureusEventParam.SEGMENT_ID.text], aureusEventContext.segmentId)
        Assert.assertEquals(parameters[AureusEventParam.BATCH_ID.text], aureusEventContext.batchId)
        Assert.assertEquals(parameters[AureusEventParam.RECOMMENDATION_ID.text], aureusEventContext.recommendationId)
    }

    @Test
    fun createClickEvent_ThenCorrectResult() {
        val eventsFactory = AureusEventFactory(snakeCaseGson, eventsFactory)

        val eventName = "eventName"

        val publicationUrl = "https://domain.com"

        val teaser = AureusTeaser("teaserId", "offerId", "contentId")

        val aureusEventContext = AureusEventContext(
            variantUuid = "4f37f85f-a8ad-4e6c-a426-5a42fce67ecc",
            batchId = "g9fewcisss",
            recommendationId = "a5uam4ufuu",
            segmentId = "uuid_word2vec_artemis_id_bisect_50_10.8",
            impressionEventType = "AUREUS_IMPRESSION_EVENT_AND_USER_ACTION",
        )

        val event = eventsFactory.createAureusClickEvent(
            selectedElementName = eventName,
            publicationUrl = URL(publicationUrl),
            teaser = teaser,
            eventContext = aureusEventContext
        )

        Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
        Assert.assertEquals(EventType.CLICK.text, event.name)
        Assert.assertEquals(event.parameters[UserEventParam.SELECTED_ELEMENT_NAME.text], eventName)
        Assert.assertEquals(event.parameters[UserEventParam.TARGET_URL.text], publicationUrl)
        Assert.assertEquals(event.parameters[UserEventParam.PAGE_VIEW_RESOURCE_IDENTIFIER.text], teaser.contentId)
        Assert.assertEquals(event.parameters[AureusEventParam.EI.text], teaser.offerId)
        Assert.assertEquals(event.parameters[AureusEventParam.ECX.text], prepareEncodedAureusEventContext())
    }

    private fun prepareEncodedAureusEventContext(): String? {
        val aureusEventContextJson =
            "{\"context\":{\"aureus\":{\"variant_uuid\":\"4f37f85f-a8ad-4e6c-a426-5a42fce67ecc\",\"batch_id\"" +
                    ":\"g9fewcisss\",\"recommendation_id\":\"a5uam4ufuu\",\"segment_id\":\"uuid_word2vec_artemis_id_bisect_50_10.8\",\"teaser_id\":\"teaserId\"}}}"
        return runCatching {
            Base64.encodeToString(aureusEventContextJson.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }.getOrNull()
    }
}
