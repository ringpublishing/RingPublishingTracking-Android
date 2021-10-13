/*
 *  Created by Grzegorz Małopolski on 9/23/21, 4:23 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.controller

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.demo.data.SampleArticle
import com.ringpublishing.tracking.demo.data.ScreenTrackingData
import com.ringpublishing.tracking.pauseContentKeepAliveTracking
import com.ringpublishing.tracking.reportPageView
import com.ringpublishing.tracking.resumeContentKeepAliveTracking
import com.ringpublishing.tracking.stopContentKeepAliveTracking
import com.ringpublishing.tracking.updateApplicationAdvertisementArea

open class ScreenController
{

	var screenTrackingData = ScreenTrackingData(listOf("Home", "List"), "ListAdsArea")

	protected var pageViewSource = ContentPageViewSource.DEFAULT

	private var article: SampleArticle? = null

	open fun prepare(source: ContentPageViewSource, sampleArticle: SampleArticle)
	{
		pageViewSource = source
		article = sampleArticle
	}

	fun updateTrackingProperties()
	{
		RingPublishingTracking.updateApplicationAdvertisementArea(screenTrackingData.advertisementArea)
	}

	open fun viewDidAppear()
	{
		// Update our tracking properties for this screen
		updateTrackingProperties()

		// Report page view event
		RingPublishingTracking.reportPageView(screenTrackingData.structurePath, false)
	}

	fun viewResumed()
	{
		// Resume keep alive tracking
		RingPublishingTracking.resumeContentKeepAliveTracking()
	}

	fun viewPaused()
	{
		// If our content controller will be obscured (not by content view) we should pause keep alive tracking and then resume it
		// when view which covered our content will be dismissed
		// To resume tracking we can call either again 'reportContentPageView' method or 'resumeContentKeepAliveTracking'
		RingPublishingTracking.pauseContentKeepAliveTracking()
	}

	fun viewStop()
	{
		// If our controller with content will not longer be presented we have to stop keep alive tracking
		RingPublishingTracking.stopContentKeepAliveTracking()
	}
}
