/*
 *  Created by Grzegorz Małopolski on 9/23/21, 4:21 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.controller

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.aureus.AureusEventContext
import com.ringpublishing.tracking.data.aureus.AureusTeaser
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
        val teaser = AureusTeaser("teaser_id_1", contentId)
        val aureusEventContext = AureusEventContext(
            clientUuid = "581ad584-2333-4e69-8963-c105184cfd04",
            variantUuid = "0e8c860f-006a-49ef-923c-38b8cfc7ca57",
            batchId = "79935e2327",
            recommendationId = "e4b25216db",
            segmentId = "group1.segment1"
        )

        RingPublishingTracking.reportContentClick(
            selectedElementName = title,
            publicationUrl = publicationUrl,
            aureusOfferId = "a4gb35",
            teaser = teaser,
            eventContext = aureusEventContext
        )
    }
}
