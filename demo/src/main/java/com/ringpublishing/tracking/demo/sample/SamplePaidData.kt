/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.demo.sample

import com.ringpublishing.tracking.data.paid.LikelihoodData
import com.ringpublishing.tracking.data.paid.MetricsData
import com.ringpublishing.tracking.data.paid.OfferContextData
import com.ringpublishing.tracking.data.paid.OfferData
import com.ringpublishing.tracking.data.paid.OfferDisplayMode
import com.ringpublishing.tracking.data.paid.PaymentMethod
import com.ringpublishing.tracking.data.paid.SubscriptionPaymentData
import com.ringpublishing.tracking.data.paid.SupplierData

val sampleSupplierData = SupplierData(
    supplierAppId = "GTccriLYpe",
    paywallSupplier = "piano"
)

val sampleOfferData = OfferData(
    supplierData = sampleSupplierData,
    paywallTemplateId = "OTT8ICJL3LWX",
    paywallVariantId = "OTVAEW37T5NG3",
    displayMode = OfferDisplayMode.INLINE
)

val sampleOfferContextData = OfferContextData(
    source = "closedArticle",
    sourcePublicationUuid = "b8b7ce67-63b8-43f6-ae47-bbfeac4002cf",
    sourceDx = "PV,puls,lb6vvn5,2,a",
    closurePercentage = 50
)

val sampleSubscriptionPaymentData = SubscriptionPaymentData(
    subscriptionBasePrice = "100",
    subscriptionPromoPrice = "99.99",
    subscriptionPromoPriceDuration = "1w",
    subscriptionPriceCurrency = "usd",
    paymentMethod = PaymentMethod.GOOGLE_PLAY
)

val sampleMetricsData = MetricsData(
    metricLimitName = "OnetMeter",
    freePvCnt = 9,
    freePvLimit = 10
)

val sampleLikelihoodData = LikelihoodData(
    lts = 5,
    ltc = 4
)