/*
 *  Created by Grzegorz Małopolski on 4/12/22, 9:55 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.data

class ClientType @JvmOverloads constructor(
    val type: ClientPlatform,
    val viewType: String? = null,
)
