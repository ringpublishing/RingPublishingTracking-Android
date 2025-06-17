/*
 *  Created by Daniel Całka on 17/6/25, 2:25 PM
 *  Copyright © 2025 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.effectivepageview.EffectivePageViewComponentSource
import com.ringpublishing.tracking.data.effectivepageview.EffectivePageViewMetadata
import com.ringpublishing.tracking.data.effectivepageview.EffectivePageViewTriggerSource
import com.ringpublishing.tracking.internal.log.Logger

/**
 * Reports effective page view event triggered by given user action.
 *
 * This event can be reported by actions such as:
 * - Scrolling through the page
 * - Playing audio or video content
 * - Viewing detail AI-generated chat summaries
 *
 * These actions are defined by pair of [EffectivePageViewComponentSource] and [EffectivePageViewTriggerSource] fields.
 *
 * This particular function handles all above scenarios except for scrolling, which is handled by the KeepAlive Reporter independently.
 *
 * This event is being reported only once per page view - for example if it has already been reported for a video play,
 * it will not be reported again for scrolling or audio play on the same page view.
 *
 * Parameters:
 * @param contentMetadata: Content Metadata related with this event. @see [ContentMetadata]
 * @param effectivePageViewComponentSource: Source of an effective page view. @see [EffectivePageViewComponentSource]
 * @param effectivePageViewTriggerSource: Code of the source of an effective page view. @see [EffectivePageViewTriggerSource]
 */
fun RingPublishingTracking.reportEffectivePageView(
    contentMetadata: ContentMetadata,
    effectivePageViewComponentSource: EffectivePageViewComponentSource,
    effectivePageViewTriggerSource: EffectivePageViewTriggerSource
) = ifInitializedOrWarn {
    val metadata = EffectivePageViewMetadata(
        componentSource = effectivePageViewComponentSource,
        triggerSource = effectivePageViewTriggerSource,
        measurement = keepAliveReporter.lastContentStatus ?: run {
            Logger.warn("EffectivePageView event is not sent because last content status is null.")
            return
        }
    )
    if (!effectivePageViewEventFactory.shouldSendEvent(metadata)) {
        Logger.warn("EffectivePageView event is not sent because it does not meet the criteria for sending.")
        return
    }
    val event = effectivePageViewEventFactory.create(contentMetadata, metadata)
    reportEvent(event)
}