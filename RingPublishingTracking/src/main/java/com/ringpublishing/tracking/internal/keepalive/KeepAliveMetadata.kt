/*
 *  Created by Grzegorz Małopolski on 10/18/21, 10:49 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import com.ringpublishing.tracking.data.KeepAliveContentStatus

internal data class KeepAliveMetadata(
    val contentStatus: KeepAliveContentStatus,
    val timingInMillis: Long,
    val hasFocus: Boolean,
    val measureType: KeepAliveMeasureType,
)
