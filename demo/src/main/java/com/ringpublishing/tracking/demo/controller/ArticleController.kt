/*
 *  Created by Grzegorz Małopolski on 9/24/21, 1:41 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.controller

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.delegate.RingPublishingTrackingKeepAliveDataSource
import com.ringpublishing.tracking.demo.data.SampleArticle
import com.ringpublishing.tracking.demo.data.ScreenTrackingData
import com.ringpublishing.tracking.reportContentPageView
import com.ringpublishing.tracking.reportEffectivePageView

class ArticleController : ScreenController()
{

	private var article: SampleArticle? = null

	init
	{
		screenTrackingData = ScreenTrackingData(listOf("Home", "Detail"), "DetailAdsArea")
	}

	fun contentViewDidAppear(contentKeepAliveDataSource: RingPublishingTrackingKeepAliveDataSource)
	{
		// Update our tracking properties for this screen
		updateTrackingProperties()

		// Report page view event
		reportArticleContentPageView(false, contentKeepAliveDataSource)
	}

	fun reloadContent(contentKeepAliveDataSource: RingPublishingTrackingKeepAliveDataSource)
	{
		// Report page view event when screen is reloaded
		// Keep alive tracking for the same content will not be interrupted
		reportArticleContentPageView(false, contentKeepAliveDataSource)
	}

	fun addMoreContent(contentKeepAliveDataSource: RingPublishingTrackingKeepAliveDataSource)
	{
		// Report page view event when screen was partially reloaded
		// Keep alive tracking for the same content will not be interrupted
		reportArticleContentPageView(true, contentKeepAliveDataSource)
	}

	private fun reportArticleContentPageView(partiallyReloaded: Boolean, contentKeepAliveDataSource: RingPublishingTrackingKeepAliveDataSource)
	{
		val articleToReport = article ?: return

		val contentMetadata = ContentMetadata(
			articleToReport.publicationId,
			articleToReport.publicationUrl,
			articleToReport.sourceSystemName,
			paidContent = articleToReport.contentWasPaidFor,
			contentId = articleToReport.contentId)

		RingPublishingTracking.reportContentPageView(
			contentMetadata,
			pageViewSource,
			screenTrackingData.structurePath,
			partiallyReloaded,
			contentKeepAliveDataSource
		)
	}

    fun reportEffectivePageView(
        effectivePageViewComponentSource: String,
        effectivePageViewTriggerSource: String,
    ) {
        val articleToReport = article ?: return

        val contentMetadata = ContentMetadata(
            articleToReport.publicationId,
            articleToReport.publicationUrl,
            articleToReport.sourceSystemName,
            paidContent = articleToReport.contentWasPaidFor,
            contentId = articleToReport.contentId
        )

        RingPublishingTracking.reportEffectivePageView(
            contentMetadata,
            effectivePageViewComponentSource,
            effectivePageViewTriggerSource
        )
    }

	fun loadArticle(newArticle: SampleArticle)
	{
		article = newArticle
	}
}
