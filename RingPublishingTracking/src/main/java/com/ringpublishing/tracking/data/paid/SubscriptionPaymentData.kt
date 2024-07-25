/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data.paid

/**
 * Data regarding subscription payment
 *
 * @param [subscriptionBasePrice]: Subscription base price
 * @param [subscriptionPromoPrice]: Subscription promotion price (optional - of someone purchases from promotion)
 * @param [subscriptionPromoPriceDuration]: Promotion duration (optional - of someone purchases from promotion) 1w / 1m / 1y etc.
 * @param [subscriptionPriceCurrency]: Purchase price currency identifier
 * @param [paymentMethod]: Payment method
 *
 * @see PaymentMethod
 */
data class SubscriptionPaymentData(
    val subscriptionBasePrice: String,
    val subscriptionPromoPrice: String?,
    val subscriptionPromoPriceDuration: String?,
    val subscriptionPriceCurrency: String,
    val paymentMethod: PaymentMethod,
)

/**
 * Payment method
 *
 * @param [text]: label
 */
enum class PaymentMethod(val text: String) {
    APP_STORE("app_store"),
    OTHER("other")
}
