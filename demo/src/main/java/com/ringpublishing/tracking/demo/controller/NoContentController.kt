/*
 *  Created by Grzegorz Małopolski on 9/24/21, 1:41 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.controller

import com.ringpublishing.tracking.demo.data.ScreenTrackingData

class NoContentController : ScreenController()
{
	init
	{
		screenTrackingData = ScreenTrackingData(listOf("Home", "NonContent"), "NonContentAdsArea")
	}
}
