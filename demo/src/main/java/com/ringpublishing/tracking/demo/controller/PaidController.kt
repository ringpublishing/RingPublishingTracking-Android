/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.demo.controller

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.demo.data.ScreenTrackingData
import com.ringpublishing.tracking.demo.sample.sampleLikelihoodData
import com.ringpublishing.tracking.demo.sample.sampleMetricsData
import com.ringpublishing.tracking.demo.sample.sampleOfferContextData
import com.ringpublishing.tracking.demo.sample.sampleOfferData
import com.ringpublishing.tracking.demo.sample.sampleSubscriptionPaymentData
import com.ringpublishing.tracking.demo.sample.sampleSupplierData
import com.ringpublishing.tracking.reportLikelihoodScoringEvent
import com.ringpublishing.tracking.reportMobileAppFakeUserIdReplacedEvent
import com.ringpublishing.tracking.reportPurchaseClickButtonEvent
import com.ringpublishing.tracking.reportPurchaseEvent
import com.ringpublishing.tracking.reportShowMetricLimitEvent
import com.ringpublishing.tracking.reportShowOfferEvent
import com.ringpublishing.tracking.reportShowOfferTeaserEvent

class PaidController : ScreenController()
{

    init
    {
        screenTrackingData = ScreenTrackingData(listOf("Home", "Paid"), "PaidAdsArea")
    }

    fun showOffer() = RingPublishingTracking.reportShowOfferEvent(
        offerData = sampleOfferData,
        offerContextData = sampleOfferContextData,
        tpcc = "hard_xmass_promoInline"
    )

    fun showOfferTeaser() = RingPublishingTracking.reportShowOfferTeaserEvent(
        offerData = sampleOfferData,
        offerContextData = sampleOfferContextData,
        tpcc = "hard_xmass_promoInline"
    )


    fun clickButton() = RingPublishingTracking.reportPurchaseClickButtonEvent(
        offerData = sampleOfferData,
        offerContextData = sampleOfferContextData,
        termId = "TMEVT00KVHV0",
        tpcc = "hard_xmass_promoInline"
    )

    fun purchase() = RingPublishingTracking.reportPurchaseEvent(
        offerData = sampleOfferData,
        offerContextData = sampleOfferContextData,
        subscriptionPaymentData = sampleSubscriptionPaymentData,
        termId = "TMEVT00KVHV0",
        termConversionId = "TCCJTS9X87VB",
        tpcc = "hard_xmass_promoInline",
        fakeUserId = "001"
    )

    fun showMetricLimit() = RingPublishingTracking.reportShowMetricLimitEvent(
        supplierData = sampleSupplierData,
        metricsData = sampleMetricsData,
        sourcePublicationUuid = "b8b7ce67-63b8-43f6-ae47-bbfeac4002cf",
        sourceDx = "PV,puls,lb6vvn5,2,a"
    )

    fun showLikelihoodScoring() = RingPublishingTracking.reportLikelihoodScoringEvent(
        supplierData = sampleSupplierData,
        likelihoodData = sampleLikelihoodData,
        sourcePublicationUuid = "b8b7ce67-63b8-43f6-ae47-bbfeac4002cf",
        sourceDx = "PV,puls,lb6vvn5,2,a"
    )

    fun replaceFakeUser() = RingPublishingTracking.reportMobileAppFakeUserIdReplacedEvent(
        fakeUserId = "fake_001",
        realUserId = "real_001"
    )
}
