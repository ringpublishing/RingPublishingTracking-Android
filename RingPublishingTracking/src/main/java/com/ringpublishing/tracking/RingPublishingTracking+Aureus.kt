/*
 *  Created by Grzegorz Małopolski on 9/21/21, 3:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Aureus events
 *
 * Reports 'Aureus' offers impression event
 *
 * @param offerIds list aureus offers
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.reportAureusOffersImpressions(offerIds: List<String>)
{
	var encoded: String? = null

	if (offerIds.isNotEmpty())
	{
		encoded = URLEncoder.encode(offerIds.joinToString(",", "[", "]") { "\"$it\"" }, StandardCharsets.UTF_8.name())
	}

	val event = eventsFactory.createUserActionEvent("aureusOfferImpressions", "offerIds", encoded)

	reportEvent(event)
}

/**
 * Reports 'Aureus' click event which leads to content page
 *
 * @param selectedElementName that user click
 * @param publicationUrl of content
 * @param publicationId publication identifier in source system (CMS)
 * @param aureusOfferId identifier
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.reportContentClick(selectedElementName: String, publicationUrl: URL, publicationId: String, aureusOfferId: String)
{
	val clickEvent = eventsFactory.createClickEvent(selectedElementName, publicationUrl, publicationId)

	clickEvent.parameters["EI"] = aureusOfferId

	reportEvent(clickEvent)
}
