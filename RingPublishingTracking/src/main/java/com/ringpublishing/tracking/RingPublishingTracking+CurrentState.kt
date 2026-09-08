/*
 *  Created by Grzegorz Małopolski on 9/21/21, 3:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

import com.ringpublishing.tracking.internal.log.Logger

/***
 *
 * Setting user identifier data as null during logout
 *
 */
fun RingPublishingTracking.logout()
{
    configurationManager.updateUserData(null, null)
}

/***
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
 *
 * Update isActiveSubscriber property
 *
 * @param isActiveSubscriber: Is user a subscriber
 */
fun RingPublishingTracking.updateActiveSubscriber(isActiveSubscriber: Boolean)
{
    configurationManager.updateIsActiveSubscriber(isActiveSubscriber)
}

/***
 *
 * Update application user identifier for tracking purpose.
 *
 * @param userId: User identifier
 * @param userEmail: User email address
 */
fun RingPublishingTracking.updateUserData(userId: String, userEmail: String?)
{
    configurationManager.updateUserData(userId, userEmail)
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

/**
 * Update variant.external parameters reported inside RDLC; rejected (unchanged) over 10 keys or 10 chars each.
 *
 * @param parameters: Map of variant.external parameters
 */
fun RingPublishingTracking.updateVariantExternalParameters(parameters: Map<String, String>) = ifInitializedOrWarn {
	Logger.debug("Updating variant external parameters: '$parameters'")

	eventsReporter.updateVariantExternalParameters(parameters)
}
