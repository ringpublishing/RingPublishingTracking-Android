package com.ringpublishing.tracking.internal.effectivepageview

/**
 * Source of an effective page view.
 */
sealed class EffectivePageViewComponentSource(val label: String) {

    /**
     * Scroll
     */
    object Scroll : EffectivePageViewComponentSource("scroll")

    /**
     * Other source with custom label.
     */
    class Other(otherLabel: String) : EffectivePageViewComponentSource(otherLabel)
}