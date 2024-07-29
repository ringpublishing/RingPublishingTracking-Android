/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data.paid

/**
 * Data regarding the offer context / content
 *
 * @param [source]: place where the offer has been displayed
 * @param [closurePercentage]: Percentage of offer being hidden by the paywall
 */
data class OfferContextData(
    val source: String,
    val closurePercentage: Int?,
)
