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
        val teaser = AureusTeaser("teaserId", "offerId", contentId)

        val aureusEventContext = AureusEventContext(
            variantUuid = "4f37f85f-a8ad-4e6c-a426-5a42fce67ecc",
            batchId = "g9fewcisss",
            recommendationId = "a5uam4ufuu",
            segmentId = "uuid_word2vec_artemis_id_bisect_50_10.8",
            impressionEventType = "AUREUS_IMPRESSION_EVENT_AND_USER_ACTION",
        )

        RingPublishingTracking.reportContentClick(
            selectedElementName = title,
            publicationUrl = publicationUrl,
            teaser = teaser,
            eventContext = aureusEventContext
        )

        val aureusEventContextNew = aureusEventContext.copy(
            impressionEventType = "AUREUS_IMPRESSION_EVENT"
        )

        RingPublishingTracking.reportContentClick(
            selectedElementName = title,
            publicationUrl = publicationUrl,
            teaser = teaser,
            eventContext = aureusEventContextNew
        )
	}
}
