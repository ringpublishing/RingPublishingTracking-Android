/*
 *  Created by Daniel Całka on 17/6/25, 2:25 PM
 *  Copyright © 2025 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data.effectivepageview

/**
 * Source of an effective page view.
 */
enum class EffectivePageViewComponentSource(val text: String) {

    /**
     * Scroll event
     */
    SCROLL("scroll"),

    /**
     * Audio play event
     */
    AUDIO("audio"),

    /**
     * Video play event
     */
    VIDEO("video"),

    /**
     * Detail ai-chat generated summary event
     */
    ONET_CHAT("onetchat"),
}