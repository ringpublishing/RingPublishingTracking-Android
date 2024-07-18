/*
 *  Created by Grzegorz Małopolski on 9/21/21, 3:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

/***
 * Dynamic tracking properties
 *
 * Setting user identifier data as null during logout
 *
 */
fun RingPublishingTracking.logout()
{
    configurationManager.updateUserData(null, null, null)
}

/***
 * Dynamic tracking properties
 *
 * Update SSO system name
 *
 * @param ssoSystemName: Name of SSO system used to login
 */
fun RingPublishingTracking.updateSSO(ssoSystemName: String)
{
    configurationManager.updateSsoSystemName(ssoSystemName)
}

/***
 * Dynamic tracking properties
 *
 * Update application user identifier for tracking purpose.
 *
 * @param ssoSystemName: Name of SSO system used to login
 * @param userId: User identifier
 * @param userEmail: User email address
 * @param isActiveSubscriber: Is user a subscriber
 */
fun RingPublishingTracking.updateUserData(ssoSystemName: String, userId: String, userEmail: String?, isActiveSubscriber: Boolean)
{
	configurationManager.updateSsoSystemName(ssoSystemName)
    configurationManager.updateUserData(userId, userEmail, isActiveSubscriber)
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
