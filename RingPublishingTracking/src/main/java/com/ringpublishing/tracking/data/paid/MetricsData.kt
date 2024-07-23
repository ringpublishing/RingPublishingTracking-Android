/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data.paid

/**
 * Metric counter data
 *
 * @param [metricLimitName]: Name of displayed metric counter
 * @param [freePvCnt]: Number of views remaining within the metric counter
 * @param [freePvLimit]: Number of free views within the metric counter
 */
data class MetricsData(
    val metricLimitName: String,
    val freePvCnt: Int,
    val freePvLimit: Int,
)