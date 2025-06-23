/*
 *  Created by Daniel Całka on 16/6/25, 2:25 PM
 *  Copyright © 2025 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.effectivepageview

/**
 * Effective Page View (ePV) event parameters.
 * @param [text] - event param text
 */
internal enum class EffectivePageViewEventParam(val text: String) {

    /**
     * ePV event version
     */
    VERSION("EV"),

    /**
     * Component generating ePV event
     */
    COMPONENT_SOURCE("ES"),

    /**
     * ePV event triggering source
     */
    TRIGGER_SOURCE("RS"),

    /**
     * Full height of details/articles content
     */
    SCROLL_HEIGHT("SH"),

    /**
     * Current contents scroll position
     */
    SCROLL_TOP("ST"),

    /**
     * Current viewport/screen height
     */
    VIEWPORT_HEIGHT("VH"),
}
