/*
 *  Created by Grzegorz Małopolski on 9/21/21, 3:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

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
	val event = eventsFactory.createAureusOffersImpressionEvent(offerIds)
	reportEvent(event)
}
