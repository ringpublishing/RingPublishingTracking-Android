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
 * @param identifier from me call
 * @param expirationDate of identifier
 */
data class TrackingIdentifier(val identifier: String, val expirationDate: Date)
