/*
 *  Created by Grzegorz Małopolski on 9/21/21, 4:18 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.delegate

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.TrackingIdentifierError
import com.ringpublishing.tracking.data.TrackingIdentifier

interface RingPublishingTrackingDelegate
{

	/**
	 * Delegate method informing when RingPublishingTracking module did set tracking identifier assigned to this device
	 * @param ringPublishingTracking object
	 * @param identifier Assigned tracking identifier
	 */
	fun ringPublishingTrackingDidAssignTrackingIdentifier(ringPublishingTracking: RingPublishingTracking, identifier: TrackingIdentifier)

	/**
	 * Delegate method informing that RingPublishingTracking module failed to assign tracking identifier.
	 * This method will be called every time there was an attempt to fetch tracking identifier but it failed
	 * (during module initialization or when another attempt to fetch tracking identifier was performed)
	 * @param ringPublishingTracking: RingPublishingTracking
	 * @param error: TrackingIdentifierError
	 */
	fun ringPublishingTrackingDidFailToRetrieveTrackingIdentifier(ringPublishingTracking: RingPublishingTracking, error: TrackingIdentifierError)
}
