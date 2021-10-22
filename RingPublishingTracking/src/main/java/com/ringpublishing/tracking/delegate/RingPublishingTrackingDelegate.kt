/*
 *  Created by Grzegorz Małopolski on 9/21/21, 4:18 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.delegate

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.TrackingIdentifier

interface RingPublishingTrackingDelegate
{

	/**
	 * Delegate method informing when RingPublishingTracking module did set tracking identifier assigned to this device
	 * @param ringPublishingTracking object
	 * @param identifier Assigned tracking identifier
	 */
	fun ringPublishingTrackingDidAssignTrackingIdentifier(ringPublishingTracking: RingPublishingTracking, identifier: TrackingIdentifier)
}
