/*
 *  Created by Grzegorz Małopolski on 9/21/21, 4:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data

/**
 * Content page view event source
 *
 */
enum class ContentPageViewSource(private val medium: String)
{

	/**
	 * Content was displayed in the app after normal user interaction, for example selecting article on the list
	 */
	DEFAULT(""),
	/**
	 * Content was displayed after opening push notification
	 */
	PUSH_NOTIFICATION("push"),
	/**
	 * Content was displayed after interacting with universal link / deep link on social media
	 */
	SOCIAL_MEDIA("social");

	fun utmMedium(): String {
		if (medium.isEmpty()) return ""
		return "?utm_medium=$medium"
	}
}
