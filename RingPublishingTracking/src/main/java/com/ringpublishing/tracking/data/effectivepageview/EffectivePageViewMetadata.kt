package com.ringpublishing.tracking.data.effectivepageview

import com.ringpublishing.tracking.data.KeepAliveContentStatus

/**
 * Metadata for an effective page view.
 * @param [componentSource] - Source of an effective page view. @see [EffectivePageViewComponentSource]
 * @param [componentSource] - Code of the source of an effective page view. @see [EffectivePageViewTriggerSource]
 * @param [measurement] - Keep alive measurement data. @see [KeepAliveContentStatus]
 */
data class EffectivePageViewMetadata(
    val componentSource: EffectivePageViewComponentSource,
    val triggerSource: EffectivePageViewTriggerSource,
    val measurement: KeepAliveContentStatus
)