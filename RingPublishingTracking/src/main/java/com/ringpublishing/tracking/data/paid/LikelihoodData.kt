/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data.paid

import com.google.gson.annotations.SerializedName

/**
 * Data regarding likelihood to subscribe / cancel subscription
 *
 * @param [likelihoodToSubscribe]: likelihood to subscribe
 * @param [likelihoodToCancel]: likelihood to cancel
 */
data class LikelihoodData(
    @SerializedName("lts") val likelihoodToSubscribe: Int,
    @SerializedName("ltc") val likelihoodToCancel: Int
)
