/*
 *  Created by Daniel Całka on 16/6/25, 2:25 PM
 *  Copyright © 2025 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

import com.google.gson.Gson
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.effectivepageview.EffectivePageViewMetadata
import com.ringpublishing.tracking.data.effectivepageview.EffectivePageViewTriggerSource
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.decorator.EventParam
import com.ringpublishing.tracking.internal.decorator.createMarkedAsPaidParam
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewEventParam
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import com.ringpublishing.tracking.internal.util.buildToDX

private const val EPV_VERSION = "1.1"

internal class EffectivePageViewEventFactory(
    private val screenSizeInfo: ScreenSizeInfo,
    private val gson: Gson,
    private val configuration: ConfigurationManager
) {

    private var isEventSent = false

    fun reset() {
        isEventSent = false
    }

    fun create(contentMetadata: ContentMetadata?, effectivePageViewMetadata: EffectivePageViewMetadata): Event {
        isEventSent = true
        return Event(AnalyticsSystem.KROPKA_STATS.text, EventType.POLARIS.text).apply {
            addContentMetaDataParams(contentMetadata)
            addEffectivePageViewMetadataParams(effectivePageViewMetadata)
        }
    }

    fun shouldSendEvent(metadata: EffectivePageViewMetadata): Boolean {
        val viewportHeight = screenSizeInfo.getScreenSizePxFromMetrics().height
        val offset = metadata.measurement.scrollOffsetPx
        val triggerSource = metadata.triggerSource

        return configuration.shouldReportEffectivePageViewEvent()
                && !isEventSent
                && (triggerSource != EffectivePageViewTriggerSource.SCROLL || (offset >= 2 * viewportHeight))
    }

    private fun Event.addContentMetaDataParams(contentMetadata: ContentMetadata?) {
        contentMetadata?.let {
            parameters[UserEventParam.PAGE_VIEW_CONTENT_INFO.text] = contentMetadata.buildToDX()
            parameters[UserEventParam.PAGE_VIEW_RESOURCE_IDENTIFIER.text] = contentMetadata.contentId.trim()
            createMarkedAsPaidParam(gson, contentMetadata)?.let { param -> parameters[EventParam.MARKED_AS_PAID_DATA.text] = param }
        }
    }

    private fun Event.addEffectivePageViewMetadataParams(metadata: EffectivePageViewMetadata) {
        val contentSizeHeight = metadata.measurement.contentSizePx.heightPx
        val offsetHeight = metadata.measurement.scrollOffsetPx
        val viewportHeight = screenSizeInfo.getScreenSizePxFromMetrics().height

        parameters[EffectivePageViewEventParam.VERSION.text] = EPV_VERSION
        parameters[EffectivePageViewEventParam.COMPONENT_SOURCE.text] = metadata.componentSource.text
        parameters[EffectivePageViewEventParam.TRIGGER_SOURCE.text] = metadata.triggerSource.text
        parameters[EffectivePageViewEventParam.SCROLL_HEIGHT.text] = contentSizeHeight
        parameters[EffectivePageViewEventParam.SCROLL_TOP.text] = offsetHeight
        parameters[EffectivePageViewEventParam.VIEWPORT_HEIGHT.text] = viewportHeight
    }
}
