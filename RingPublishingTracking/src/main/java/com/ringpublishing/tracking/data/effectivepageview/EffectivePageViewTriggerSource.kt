/*
 *  Created by Daniel Całka on 17/6/25, 2:25 PM
 *  Copyright © 2025 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data.effectivepageview

/**
 * Code of the source of an effective page view.
 */
enum class EffectivePageViewTriggerSource(val text: String) {

    /**
     * Scroll
     */
    SCROLL("scrl"),

    /**
     * Play (audio or video)
     */
    PLAY("play"),

    /**
     * Detail ai-chat generated summary
     */
    SUMMARY("summary"),

}