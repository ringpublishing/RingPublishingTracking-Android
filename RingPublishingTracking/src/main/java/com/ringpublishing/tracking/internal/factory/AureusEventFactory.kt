package com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.Gson
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.aureus.AureusEventContext
import com.ringpublishing.tracking.data.aureus.AureusTeaser
import com.ringpublishing.tracking.internal.aureus.AureusEventParam
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.log.Logger
import java.net.URL

class AureusEventFactory(private val snakeCaseGson: Gson)
{
    fun createAureusImpressionEvent(teasers: List<AureusTeaser>, eventContext: AureusEventContext): Event
    {
        val parameters = mutableMapOf<String, Any>()

        parameters[AureusEventParam.DISPLAYED_ITEMS.text] = snakeCaseGson.toJson(teasers)
        eventContext.clientUuid?.let { parameters[AureusEventParam.CLIENT_UUID.text] = it }
        eventContext.variantUuid?.let { parameters[AureusEventParam.VARIANT_UUID.text] = it }
        eventContext.segmentId?.let { parameters[AureusEventParam.SEGMENT_ID.text] = it }
        eventContext.batchId?.let { parameters[AureusEventParam.BATCH_ID.text] = it }
        eventContext.recommendationId?.let { parameters[AureusEventParam.RECOMMENDATION_ID.text] = it }


        return Event(AnalyticsSystem.GENERIC.text, EventType.AUREUS_IMPRESSION_EVENT.text, parameters)
    }

    fun createAureusClickEvent(selectedEventName: String? = null,
                               publicationUrl: URL? = null,
                               aureusOfferId: String? = null,
                               teaser: AureusTeaser? = null,
                               eventContext: AureusEventContext? = null): Event
    {
        val parameters = mutableMapOf<String, Any>()

        eventContext?.teaserId = teaser?.teaserId

        selectedEventName?.let { parameters[UserEventParam.SELECTED_ELEMENT_NAME.text] = it }
        publicationUrl?.let { parameters[UserEventParam.TARGET_URL.text] = it.toString() }
        teaser?.contentId?.let { parameters[UserEventParam.PAGE_VIEW_RESOURCE_IDENTIFIER.text] = it }
        aureusOfferId?.let { parameters[AureusEventParam.EI.text] = it }
        eventContext?.prepareEcxParameter()?.let {  ecx ->
            parameters[AureusEventParam.ECX.text] = ecx
        }

        return Event(AnalyticsSystem.KROPKA_EVENTS.text, EventType.CLICK.text, parameters)
    }

    private fun AureusEventContext.prepareEcxParameter(): String?
    {
        val parameter = AureusEventContextWrapper(AureusContext(this))
        val jsonParam = snakeCaseGson.toJson(parameter)

        return runCatching {
            Base64.encodeToString(jsonParam.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }.getOrElse {
            Logger.warn("Parse AureusContext UnsupportedEncodingException ${it.localizedMessage}")
            null
        }
    }

    private class AureusEventContextWrapper(
        private val context: AureusContext
    )

    private class AureusContext(
        private val aureus: AureusEventContext
    )
}
