/*
 *  Created by Grzegorz Małopolski on 9/21/21, 3:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.aureus.AureusEventContext
import com.ringpublishing.tracking.data.aureus.AureusTeaser
import com.ringpublishing.tracking.internal.aureus.ImpressionEventType
import com.ringpublishing.tracking.internal.log.Logger
import java.net.URL

/**
 * Aureus events
 *
 * Reports 'Aureus' impression event
 *
 * @param teasers list of AureusTeaser instances
 * @param eventContext AureusContext instance
 */
@Suppress("unused")
fun RingPublishingTracking.reportAureusImpression(
    teasers: List<AureusTeaser>,
    eventContext: AureusEventContext
) {
    Logger.debug("Reporting 'Aureus' offers impression event for teasers: $teasers")
    when (eventContext.impressionEventType.uppercase()) {
        ImpressionEventType.USER_ACTION.name -> {
            reportEvent(
                event = aureusEventFactory.createLegacyAureusImpressionEvent(teasers)
            )
        }

        ImpressionEventType.AUREUS_IMPRESSION_EVENT_AND_USER_ACTION.name -> {
            reportEvent(
                event = aureusEventFactory.createLegacyAureusImpressionEvent(teasers)
            )
            reportEvent(
                event = aureusEventFactory.createNewAureusImpressionEvent(teasers, eventContext)
            )
        }

        else -> reportEvent(
            event = aureusEventFactory.createNewAureusImpressionEvent(teasers, eventContext)
        )
    }
}

/**
 * Reports 'Aureus' click event which leads to content page
 *
 * @param selectedElementName name of the element that user clicked
 * @param publicationUrl url of content
 * @param teaser AureusTeaser instance
 * @param eventContext AureusContext instance
 */
fun RingPublishingTracking.reportContentClick(
    selectedElementName: String,
    publicationUrl: URL,
    teaser: AureusTeaser,
    eventContext: AureusEventContext
) {
    val clickEvent = aureusEventFactory.createAureusClickEvent(
        selectedElementName = selectedElementName,
        publicationUrl = publicationUrl,
        teaser = teaser,
        eventContext = eventContext
    )
    reportEvent(clickEvent)
}
