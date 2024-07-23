/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data.paid

/**
 * Data regarding likelihood to subscribe / cancel subscription
 *
 * @param [lts]: likelihood to subscribe
 * @param [ltc]: likelihood to cancel
 */
data class LikelihoodData(
    val lts: Int,
    val ltc: Int
)