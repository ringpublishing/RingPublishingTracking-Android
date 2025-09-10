package com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.ringpublishing.tracking.RingPublishingTracking.eventsFactory
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.aureus.AureusEventContext
import com.ringpublishing.tracking.data.aureus.AureusTeaser
import com.ringpublishing.tracking.internal.aureus.AureusEventParam
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.log.Logger
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class AureusEventFactory(
    private val snakeCaseGson: Gson,
    private val eventFactory: EventsFactory
) {
    fun createNewAureusImpressionEvent(
        teasers: List<AureusTeaser>,
        eventContext: AureusEventContext
    ): Event {

        val parameters = mutableMapOf<String, Any>()

        val displayedItemsJsonArray = runCatching {
            snakeCaseGson.fromJson(
                snakeCaseGson.toJson(teasers.map { AureusEventTeaserWrapper(it) }),
                JsonArray::class.java
            )
        }.getOrNull()

        val events = mutableMapOf<String, Any>(
            AureusEventParam.VARIANT_UUID.text to eventContext.variantUuid,
            AureusEventParam.SEGMENT_ID.text to eventContext.segmentId,
            AureusEventParam.BATCH_ID.text to eventContext.batchId,
            AureusEventParam.RECOMMENDATION_ID.text to eventContext.recommendationId
        ).apply {
            displayedItemsJsonArray?.let {
                this[AureusEventParam.DISPLAYED_ITEMS.text] = it
            }
        }

        val eventsJsonArray = runCatching {
            snakeCaseGson.fromJson(
                snakeCaseGson.toJson(listOf(events)),
                JsonArray::class.java
            )
        }.getOrNull()

        eventsJsonArray?.let {
            parameters.put(AureusEventParam.EVENTS.text, it)
        }

        return Event(
            analyticsSystemName = AnalyticsSystem.GENERIC.text,
            name = EventType.AUREUS_IMPRESSION_EVENT.text,
            parameters = parameters
        )
    }

    fun createLegacyAureusImpressionEvent(
        teasers: List<AureusTeaser>,
    ): Event {
        val offerIds = teasers.mapNotNull { it.offerId }.distinct()

        val encoded = offerIds.takeIf { it.isNotEmpty() }
            ?.joinToString(",", "[", "]") { "\"$it\"" }
            ?.let { URLEncoder.encode(it, StandardCharsets.UTF_8.name()) }

        return eventsFactory.createUserActionEvent(
            actionName = "aureusOfferImpressions",
            actionSubtypeName = "offerIds",
            parametersString = encoded
        )
    }

    fun createAureusClickEvent(
        selectedElementName: String,
        publicationUrl: URL,
        teaser: AureusTeaser,
        eventContext: AureusEventContext
    ): Event {
        val clickEvent = eventFactory.createClickEvent(
            selectedEventName = selectedElementName,
            publicationUrl = publicationUrl,
            contentIdentifier = teaser.contentId
        ).apply {
            teaser.offerId?.let {
                parameters[AureusEventParam.EI.text] = it
            }

            val updatedContext = eventContext.copy().apply {
                this.teaserId = teaser.teaserId
            }

            updatedContext.prepareEcxParameter()?.let {
                parameters[AureusEventParam.ECX.text] = it
            }
        }

        return clickEvent
    }

    private fun AureusEventContext.prepareEcxParameter(): String? {
        val parameter = AureusEventContextWrapper(
            context = AureusContext(
                aureus = AureusEventContextParams(this)
            )
        )
        val jsonParam = snakeCaseGson.toJson(parameter)

        return runCatching {
            Base64.encodeToString(jsonParam.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }.getOrElse {
            Logger.warn("Parse AureusContext UnsupportedEncodingException ${it.localizedMessage}")
            null
        }
    }

    @Suppress("unused")
    internal class AureusEventTeaserWrapper(
        val teaserId: String?,
        val contentId: String
    ) {
        constructor(teaser: AureusTeaser) : this(
            teaserId = teaser.teaserId,
            contentId = teaser.contentId
        )
    }

    @Suppress("unused")
    private class AureusEventContextWrapper(
        private val context: AureusContext
    )

    @Suppress("unused")
    private class AureusContext(
        private val aureus: AureusEventContextParams
    )

    @Suppress("unused")
    private class AureusEventContextParams(
        private val variantUuid: String,
        private val batchId: String,
        private val recommendationId: String,
        private val segmentId: String,
        private val teaserId: String?
    ) {
        constructor(context: AureusEventContext) : this(
            variantUuid = context.variantUuid,
            batchId = context.batchId,
            recommendationId = context.recommendationId,
            segmentId = context.segmentId,
            teaserId = context.teaserId
        )
    }
}
