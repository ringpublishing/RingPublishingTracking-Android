/*
 *  Created by Grzegorz Małopolski on 10/20/21, 2:10 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.data

import java.util.Date

/**
 * Tracking identifier
 *
 * @param userIdentifier base tracking identifier
 * @param artemisIdentifier artemis tracking identifier
 */
data class TrackingIdentifier(
    val userIdentifier: UserIdentifier,
    val artemisIdentifier: ArtemisIdentifier
)

data class UserIdentifier(val identifier: String, val expirationDate: Date)

data class ArtemisIdentifier(val identifier: ArtemisId, val expirationDate: Date)
