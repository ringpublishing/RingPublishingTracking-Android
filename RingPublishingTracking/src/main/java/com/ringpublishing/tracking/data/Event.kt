/*
 *  Created by Grzegorz Małopolski on 9/21/21, 4:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data

data class Event(
    val analyticsSystemName: String,
    val name: String,
    val parameters: Map<String, Any>,
)
