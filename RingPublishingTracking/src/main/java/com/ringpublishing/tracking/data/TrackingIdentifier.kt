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
 * @param eaUUID base tracking identifier
 * @param artemisID artemis tracking identifier
 */
data class TrackingIdentifier(
    val eaUUID: Identifier,
    val artemisID: Identifier
)

data class Identifier(val value: String, val expirationDate: Date)
