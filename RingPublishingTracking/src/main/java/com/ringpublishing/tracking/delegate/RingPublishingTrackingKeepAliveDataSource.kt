/*
 *  Created by Grzegorz Małopolski on 9/21/21, 4:19 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.delegate

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.KeepAliveContentStatus

interface RingPublishingTrackingKeepAliveDataSource
{

	/**
	 * Data source method asking for updated status for tracked content metadata
	 * @return KeepAliveContentStatus
	 */
	fun ringPublishingTrackingDidAskForKeepAliveContentStatus(ringPublishingTracking: RingPublishingTracking, contentMetadata: ContentMetadata): KeepAliveContentStatus
}
