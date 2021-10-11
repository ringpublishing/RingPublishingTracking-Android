/*
 *  Created by Grzegorz Małopolski on 9/21/21, 4:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data

import com.ringpublishing.tracking.internal.constants.Constants

data class Event(
    val analyticsSystemName: String = Constants.eventDefaultAnalyticsSystemName,
    val name: String = Constants.eventDefaultName,
    val parameters: MutableMap<String, Any> = mutableMapOf(),
)
