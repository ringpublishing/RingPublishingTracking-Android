/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.demo.controller

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.demo.data.ScreenTrackingData
import com.ringpublishing.tracking.demo.sample.sampleContentMetadata
import com.ringpublishing.tracking.demo.sample.sampleLikelihoodData
import com.ringpublishing.tracking.demo.sample.sampleMetricsData
import com.ringpublishing.tracking.demo.sample.sampleOfferContextData
import com.ringpublishing.tracking.demo.sample.sampleOfferData
import com.ringpublishing.tracking.demo.sample.sampleSubscriptionPaymentData
import com.ringpublishing.tracking.demo.sample.sampleSupplierData
import com.ringpublishing.tracking.reportLikelihoodScoringEvent
import com.ringpublishing.tracking.reportMobileAppTemporaryUserIdReplacedEvent
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
        contentMetadata = sampleContentMetadata,
        offerData = sampleOfferData,
        offerContextData = sampleOfferContextData,
        targetPromotionCampaignCode = "hard_xmass_promoInline"
    )

    fun showOfferTeaser() = RingPublishingTracking.reportShowOfferTeaserEvent(
        contentMetadata = sampleContentMetadata,
        offerData = sampleOfferData,
        offerContextData = sampleOfferContextData,
        targetPromotionCampaignCode = "hard_xmass_promoInline"
    )

    fun clickButton() = RingPublishingTracking.reportPurchaseClickButtonEvent(
        contentMetadata = sampleContentMetadata,
        offerData = sampleOfferData,
        offerContextData = sampleOfferContextData,
        termId = "TMEVT00KVHV0",
        targetPromotionCampaignCode = "hard_xmass_promoInline"
    )

    fun purchase() = RingPublishingTracking.reportPurchaseEvent(
        contentMetadata = sampleContentMetadata,
        offerData = sampleOfferData,
        offerContextData = sampleOfferContextData,
        subscriptionPaymentData = sampleSubscriptionPaymentData,
        termId = "TMEVT00KVHV0",
        termConversionId = "TCCJTS9X87VB",
        targetPromotionCampaignCode = "hard_xmass_promoInline",
        temporaryUserId = "001"
    )

    fun showMetricLimit() = RingPublishingTracking.reportShowMetricLimitEvent(
        contentMetadata = sampleContentMetadata,
        supplierData = sampleSupplierData,
        metricsData = sampleMetricsData,
    )

    fun showLikelihoodScoring() = RingPublishingTracking.reportLikelihoodScoringEvent(
        contentMetadata = sampleContentMetadata,
        supplierData = sampleSupplierData,
        likelihoodData = sampleLikelihoodData,
    )

    fun replaceFakeUser() = RingPublishingTracking.reportMobileAppTemporaryUserIdReplacedEvent(
        contentMetadata = sampleContentMetadata,
        temporaryUserId = "fake_001",
        realUserId = "real_001"
    )
}
