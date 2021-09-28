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
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.updateUserData(ssoSystemName: String, userId: String?)
{
	// todo: Implement
}

/**
 * Update ad space name of the application, for example "ads/list/sport"
 *
 * @param currentAdvertisementArea: String
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.updateApplicationAdvertisementArea(currentAdvertisementArea: String)
{
	// TODO: Implementation missing
}
