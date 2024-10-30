/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.demo.sample

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.paid.LikelihoodData
import com.ringpublishing.tracking.data.paid.MetricsData
import com.ringpublishing.tracking.data.paid.OfferContextData
import com.ringpublishing.tracking.data.paid.OfferData
import com.ringpublishing.tracking.data.paid.OfferDisplayMode
import com.ringpublishing.tracking.data.paid.PaymentMethod
import com.ringpublishing.tracking.data.paid.SubscriptionPaymentData
import com.ringpublishing.tracking.data.paid.SupplierData
import java.net.URL

val sampleSupplierData = SupplierData(
    supplierAppId = "GTccriLYpe",
    paywallSupplier = "piano"
)

val sampleOfferData = OfferData(
    supplierData = sampleSupplierData,
    paywallTemplateId = "OTT8ICJL3LWX",
    paywallVariantId = "OTVAEW37T5NG3",
    displayMode = OfferDisplayMode.INLINE,
    "standard-promo"
)

val sampleOfferContextData = OfferContextData(
    source = "closedArticle",
    closurePercentage = 50
)

val sampleSubscriptionPaymentData = SubscriptionPaymentData(
    subscriptionBasePrice = 100.0F,
    subscriptionPromoPrice = 99.99F,
    subscriptionPromoDuration = "1W",
    subscriptionPriceCurrency = "usd",
    paymentMethod = PaymentMethod.GOOGLE_PLAY
)

val sampleMetricsData = MetricsData(
    metricLimitName = "OnetMeter",
    freePageViewCount = 9,
    freePageViewLimit = 10
)

val sampleLikelihoodData = LikelihoodData(
    likelihoodToSubscribe = 5,
    likelihoodToCancel = 4
)

val sampleContentMetadata = ContentMetadata(
    publicationId = "publicationId",
    publicationUrl = URL("https://domain.com"),
    sourceSystemName = "source System_Name",
    contentPartIndex = 1,
    paidContent = true,
    contentId = "my-unique-content-id-1234"
)
