package com.ringpublishing.tracking.internal.effectivepageview

/**
 * Code of the source of an effective page view.
 */
sealed class EffectivePageViewTriggerSource(val label: String) {

    /**
     * Scroll
     */
    object Scroll : EffectivePageViewTriggerSource("scrl")

    /**
     * Other code source with custom label.
     */
    class Other(otherLabel: String) : EffectivePageViewTriggerSource(otherLabel)
}