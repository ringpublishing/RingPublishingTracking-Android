/*
 *  Created by Grzegorz Małopolski on 9/21/21, 3:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

/***
 * Dynamic tracking properties
 *
 * Update application user identifier for tracking purpose.
 * If user is not logged in, pass nil as 'userId'.
 *
 * @param ssoSystemName: Name of SSO system used to login
 * @param userId: User identifier
 * @param userEmail: User email address
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.updateUserData(ssoSystemName: String, userId: String?, userEmail: String?)
{
	configurationManager.updateUserData(ssoSystemName, userId, userEmail)
}

/**
 * Update ad space name of the application, for example "ads/list/sport"
 *
 * @param currentAdvertisementArea: String
 */
fun RingPublishingTracking.updateApplicationAdvertisementArea(currentAdvertisementArea: String)
{
	configurationManager.updateAdvertisementArea(currentAdvertisementArea)
}
