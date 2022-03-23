/*
 *  Created by Grzegorz Małopolski on 9/23/21, 4:21 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.controller

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.demo.data.ScreenTrackingData
import com.ringpublishing.tracking.reportContentClick
import java.net.URL

class ListArticlesController : ScreenController()
{

	init
	{
		screenTrackingData = ScreenTrackingData(listOf("Home", "List"), "ListAdsArea")
	}

	fun listItemClick(title: String, publicationUrl: URL, contentId: String)
	{
		RingPublishingTracking.reportContentClick(title, publicationUrl, contentId, aureusOfferId = "a4gb35")
	}
}
