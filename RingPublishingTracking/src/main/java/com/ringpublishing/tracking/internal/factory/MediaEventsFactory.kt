package com.ringpublishing.tracking.internal.factory

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.media.MediaContentState
import com.ringpublishing.tracking.internal.media.MediaEventDetails
import com.ringpublishing.tracking.internal.media.MediaEventParam
import com.ringpublishing.tracking.internal.media.MediaEventType
import com.ringpublishing.tracking.internal.media.MediaSessionState

internal class MediaEventsFactory {

    fun createMediaEvent(
        eventType: MediaEventType,
        session: MediaSessionState,
        content: MediaContentState?,
        details: MediaEventDetails
    ): Event {
        val parameters = mutableMapOf<String, Any>().apply {
            this[MediaEventParam.EVENT_TYPE_RT.text] = EventType.MEDIA.text
            this[MediaEventParam.EVENT_TYPE.text] = eventType.text
            this[MediaEventParam.EVENT_ORDINAL.text] = session.eventOrdinal
            this[MediaEventParam.MEDIA_SESSION_ID.text] = session.sessionId

            addSessionParameters(session)
            addContentParameters(content)
            addDetailsParameters(details)
        }

        return Event(
            analyticsSystemName = AnalyticsSystem.GENERIC.text,
            name = EventType.MEDIA.text,
            parameters = parameters
        )
    }

    private fun MutableMap<String, Any>.addSessionParameters(session: MediaSessionState) {
        this[MediaEventParam.MEDIA_TYPE.text] = session.mediaType.text
        put(MediaEventParam.PLAYER_VARIANT, session.playerVariant)
        this[MediaEventParam.AUDIO_OUTPUT.text] = session.audioOutput.text
        this[MediaEventParam.MUTED.text] = session.muted
        this[MediaEventParam.PLAYBACK_RATE.text] = session.playbackRate
        this[MediaEventParam.VISIBILITY_STATE.text] = session.visibilityState.text
    }

    private fun MutableMap<String, Any>.addContentParameters(content: MediaContentState?) {
        this[MediaEventParam.USER_ENGAGED.text] = content?.userEngaged ?: false
        content ?: return

        put(MediaEventParam.AD_CAMPAIGN_ID, content.adCampaignId)
        put(MediaEventParam.AD_CREATIVE_ID, content.adCreativeId)
        put(MediaEventParam.AD_ID, content.adId)
        put(MediaEventParam.AD_SLOT, content.adSlot)
        put(MediaEventParam.DURATION_SEC, content.durationSec)
        put(MediaEventParam.MEDIA_CONTENT_SUBTYPE, content.mediaContentSubtype?.text)
        put(MediaEventParam.MEDIA_CONTENT_TYPE, content.mediaContentType?.text)
        put(MediaEventParam.MEDIA_ID, content.mediaId)
        put(MediaEventParam.MEDIA_PREMIUM, content.mediaPremium)
        put(MediaEventParam.MEDIA_TIER, content.mediaTier?.text)
        put(MediaEventParam.MEDIA_VARIANT, content.mediaVariant)
        put(MediaEventParam.MONETIZATION_TYPE, content.monetizationType?.text)
        put(MediaEventParam.PLAYBACK_INTENT, content.playbackIntent?.text)
        put(MediaEventParam.RELATED_MEDIA_ID, content.relatedMediaId)
        put(MediaEventParam.SUBSCRIPTION_TIER, content.subscriptionTier?.text)
    }

    private fun MutableMap<String, Any>.addDetailsParameters(details: MediaEventDetails) {
        put(MediaEventParam.POSITION_SEC, details.positionSec)
        put(MediaEventParam.START_MODE, details.startMode?.text)
        put(MediaEventParam.PAUSE_REASON, details.pauseReason?.text)
        put(MediaEventParam.PLAYBACK_END_REASON, details.playbackEndReason?.text)
        put(MediaEventParam.ERROR_CODE, details.errorCode)
        put(MediaEventParam.ERROR_MESSAGE, details.errorMessage)
    }

    private fun MutableMap<String, Any>.put(param: MediaEventParam, value: Any?) {
        if (value != null) this[param.text] = value
    }
}
