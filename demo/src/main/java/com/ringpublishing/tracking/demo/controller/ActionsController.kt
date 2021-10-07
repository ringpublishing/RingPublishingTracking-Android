/*
 *  Created by Grzegorz Małopolski on 9/23/21, 1:51 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.controller

import android.view.View
import android.widget.Button
import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.demo.data.ScreenTrackingData
import com.ringpublishing.tracking.reportAureusOffersImpressions
import com.ringpublishing.tracking.reportClick
import com.ringpublishing.tracking.reportUserAction
import com.ringpublishing.tracking.updateUserData

class ActionsController : ScreenController()
{

	init
	{
		screenTrackingData = ScreenTrackingData(listOf("Home", "Actions"), "ActionsAdsArea")
	}

	fun actionLogin(sender: View)
	{
		// When user log in we should update tracking module with his account information
		RingPublishingTracking.updateUserData("RingPublishingTrackingSSO", "12345")

		// Each non content button click we can report using 'reportClick' method
		reportButtonClickEvent(sender)
	}

	fun actionLogout(sender: View)
	{
		// When user log out from the application we should update tracking module
		RingPublishingTracking.updateUserData("RingPublishingTrackingSSO", null)

		// Each non content button click we can report using 'reportClick' method
		reportButtonClickEvent(sender)
	}

	fun actionReportUserActionString(sender: View)
	{
		// Each user action which you want to track can be reported with String parameters
		// This could be either plain string or encoded JSON prepared by your app
		val userActionPayload = "in_app_purchase_product=product1;value=20"
		RingPublishingTracking.reportUserAction("UserPurchase", "In-app purchase", userActionPayload)

		// Each non content button click we can report using 'reportClick' method
		reportButtonClickEvent(sender)
	}

	fun actionReportUserActionMap(sender: View)
	{
		// Each user action which you want to track can be reported with Dictionary parameters
		// This will be encoded to JSON by RingPublishingTracking module
		val parameters = mutableMapOf<String, Any>()
		parameters["in_app_purchase_product"] = "product1"
		parameters["value"] = 20

		RingPublishingTracking.reportUserAction("UserPurchase", "In-app purchase", parameters)

		// Each non content button click we can report using 'reportClick' method
		reportButtonClickEvent(sender)
	}

	fun actionOpenDetailFromPush()
	{
		// If you know in your application if your content was opened from push notification
		// (and not in usually way directly from the app), you can pass that information to tracking module
		pageViewSource = ContentPageViewSource.PUSH_NOTIFICATION
	}

	fun actionOpenDetailFromSocial()
	{
		// If you know in your application if your content was opened from social media, for example
		// using universal links (and not in usual way directly from the app).
		// You can pass that information to tracking module
		pageViewSource = ContentPageViewSource.SOCIAL_MEDIA
	}

	fun actionReportGenericEvent()
	{
		// If you have the need to report custom event (which is not defined in module interface) you can
		// always do that using generic 'reportEvent' method, but you must construct Event yourself and know
		// it's parameters
		val customEvent = Event("GENERIC", "DemoCustomEvent", mutableMapOf("myParam" to "myValue"))
		RingPublishingTracking.reportEvent(customEvent)
	}

	fun actionEnableDebugMode()
	{
		// If you don't want to send events to API during development (or for some other reason) but you still want
		// to see events being processed by the SDK, you can enable debug mode
		RingPublishingTracking.setDebugMode(true)
	}

	fun actionDisableDebugMode()
	{
		// You can always disable debug mode
		RingPublishingTracking.setDebugMode(false)
	}

	fun actionEnableOptOutMode()
	{
		// If you don't want to send events to API and also don't want to see events being processed by the SDK,
		// you can enable opt-out mode. In this mode, SDK functionality is disabled (events processing, logger, API calls)
		RingPublishingTracking.setOptOutMode(true)
	}

	fun actionDisableOptOutMode()
	{
		// If you don't want to send events to API and also don't want to see events being processed by the SDK,
		// you can enable opt-out mode. In this mode, SDK functionality is disabled (events processing, logger, API calls)
		RingPublishingTracking.setOptOutMode(false)
	}

	fun actionReportAureusImpression()
	{
		// If you have recommendations delivered by personalization engine (Aureus) you should report
		// when those items are displayed to the user

		val offerIds = listOf("123", "456", "789")
		RingPublishingTracking.reportAureusOffersImpressions(offerIds)
	}

	private fun reportButtonClickEvent(sender: View)
	{
		// If our click action does not have a name, we can omit it
		val actionName = when (sender)
		{
			is Button -> sender.text.toString()
			else -> null
		}

		actionName?.let {
			RingPublishingTracking.reportClick(actionName)
		}
	}
}
