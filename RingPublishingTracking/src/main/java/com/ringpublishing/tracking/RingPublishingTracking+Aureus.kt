/*
 *  Created by Grzegorz Małopolski on 9/21/21, 3:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.aureus.AureusEventContext
import com.ringpublishing.tracking.data.aureus.AureusTeaser
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
fun RingPublishingTracking.reportAureusImpression(teasers: List<AureusTeaser>, eventContext: AureusEventContext)
{
	val event = aureusEventFactory.createAureusImpressionEvent(teasers, eventContext)
	reportEvent(event)
}

/**
 * Reports 'Aureus' click event which leads to content page
 *
 * @param selectedElementName that user click
 * @param publicationUrl of content
 * @param aureusOfferId identifier
 * @param teaser AureusTeaser instance
 * @param eventContext AureusContext instance
 */
@Suppress("unused")
fun RingPublishingTracking.reportContentClick(selectedElementName: String, publicationUrl: URL, aureusOfferId: String, teaser: AureusTeaser, eventContext: AureusEventContext)
{
    val clickEvent = aureusEventFactory.createAureusClickEvent(selectedElementName, publicationUrl, aureusOfferId, teaser, eventContext)
    reportEvent(clickEvent)
}
