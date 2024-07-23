/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.paid.LikelihoodData
import com.ringpublishing.tracking.data.paid.MetricsData
import com.ringpublishing.tracking.data.paid.OfferContextData
import com.ringpublishing.tracking.data.paid.OfferData
import com.ringpublishing.tracking.data.paid.SubscriptionPaymentData
import com.ringpublishing.tracking.data.paid.SupplierData

/**
 * Reports showing offer event
 * There is possibility to start purchasing process flow from this place
 *
 * @param [contentMetadata]: Content metadata
 * @param [offerData]: Data regarding the supplier of sales offers and offers themselves
 * @param [offerContextData]: Data regarding the offer context / content
 * @param [tpcc]: Offer id of given promotion / campaign
 *
 * @see ContentMetadata
 * @see OfferData
 * @see OfferContextData
 */
fun RingPublishingTracking.reportShowOfferEvent(
    contentMetadata: ContentMetadata,
    offerData: OfferData,
    offerContextData: OfferContextData,
    tpcc: String?,
) = reportEvent(paidEventsFactory.createShowOfferEvent(contentMetadata, offerData, offerContextData, tpcc))

/**
 * Reports showing offer teaser event
 * There is no possibility to start purchasing process flow from this place
 *
 * @param [contentMetadata]: Content metadata
 * @param [offerData]: Data regarding the supplier of sales offers and offers themselves
 * @param [offerContextData]: Data regarding the offer context / content
 * @param [tpcc]: Offer id of given promotion / campaign
 *
 * @see ContentMetadata
 * @see OfferData
 * @see OfferContextData
 */
fun RingPublishingTracking.reportShowOfferTeaserEvent(
    contentMetadata: ContentMetadata,
    offerData: OfferData,
    offerContextData: OfferContextData,
    tpcc: String?,
) = reportEvent(paidEventsFactory.createShowOfferTeaserEvent(contentMetadata, offerData, offerContextData, tpcc))

/**
 * Reports event of clicking button used to start purchasing process flow
 *
 * @param [contentMetadata]: Content metadata
 * @param [offerData]: Data regarding the supplier of sales offers and offers themselves
 * @param [offerContextData]: Data regarding the offer context / content
 * @param [termId]: Id of specific purchase term / offer selected by user
 * @param [tpcc]: Offer id of given promotion / campaign
 *
 * @see ContentMetadata
 * @see OfferData
 * @see OfferContextData
 */
fun RingPublishingTracking.reportPurchaseClickButtonEvent(
    contentMetadata: ContentMetadata,
    offerData: OfferData,
    offerContextData: OfferContextData,
    termId: String,
    tpcc: String?,
) = reportEvent(paidEventsFactory.createPurchaseClickButtonEvent(contentMetadata, offerData, offerContextData, termId, tpcc))

/**
 * Reports subscription purchase event
 *
 * @param [contentMetadata]: Content metadata
 * @param [offerData]: Data regarding the supplier of sales offers and offers themselves
 * @param [offerContextData]: Data regarding the offer context / content
 * @param [subscriptionPaymentData]: Data regarding subscription payment
 * @param [termId]: Id of specific purchase term / offer selected by user
 * @param [termConversionId]: Purchase conversion id
 * @param [tpcc]: Offer id of given promotion / campaign
 * @param [fakeUserId]: previous fake user id
 *
 * @see ContentMetadata
 * @see OfferData
 * @see OfferContextData
 * @see SubscriptionPaymentData
 */
fun RingPublishingTracking.reportPurchaseEvent(
    contentMetadata: ContentMetadata,
    offerData: OfferData,
    offerContextData: OfferContextData,
    subscriptionPaymentData: SubscriptionPaymentData,
    termId: String,
    termConversionId: String,
    tpcc: String?,
    fakeUserId: String?
) = reportEvent(paidEventsFactory.createPurchaseEvent(contentMetadata, offerData, offerContextData, subscriptionPaymentData, termId, termConversionId, tpcc, fakeUserId))

/**
 * Reports event of displaying paid content to the user within a metered counter.
 *
 * @param [contentMetadata]: Content metadata
 * @param [supplierData]: Data regarding the supplier of sales
 * @param [metricsData]: Metric counter data
 * @param [sourcePublicationUuid]: Id of the publication where the offer has been displayed
 * @param [sourceDx]: DX parameter
 *
 * @see ContentMetadata
 * @see SupplierData
 * @see MetricsData
 */
fun RingPublishingTracking.reportShowMetricLimitEvent(
    contentMetadata: ContentMetadata,
    supplierData: SupplierData,
    metricsData: MetricsData,
    sourcePublicationUuid: String,
    sourceDx: String,
) = reportEvent(paidEventsFactory.createShowMetricLimitEvent(contentMetadata, supplierData, metricsData, sourcePublicationUuid, sourceDx))

/**
 * Reports event of piano prediction of user likelihood to subscribe / cancel subscription
 *
 * @param [contentMetadata]: Content metadata
 * @param [supplierData]: Data regarding the supplier of sales
 * @param [likelihoodData]: Data regarding likelihood to subscribe / cancel subscription
 * @param [sourcePublicationUuid]: Id of the publication where the offer has been displayed
 * @param [sourceDx]: DX parameter
 *
 * @see ContentMetadata
 * @see SupplierData
 * @see LikelihoodData
 */
fun RingPublishingTracking.reportLikelihoodScoringEvent(
    contentMetadata: ContentMetadata,
    supplierData: SupplierData,
    likelihoodData: LikelihoodData,
    sourcePublicationUuid: String,
    sourceDx: String
) = reportEvent(paidEventsFactory.createLikelihoodScoringEvent(contentMetadata, supplierData, likelihoodData, sourcePublicationUuid, sourceDx))

/**
 * Reports event about changing user data from fake to real
 *
 * @param [contentMetadata]: Content metadata
 * @param [fakeUserId]: previous fake user id
 * @param [realUserId]: new user id
 *
 * @see ContentMetadata
 */
fun RingPublishingTracking.reportMobileAppFakeUserIdReplacedEvent(
    contentMetadata: ContentMetadata,
    fakeUserId: String,
    realUserId: String
) = reportEvent(paidEventsFactory.createMobileAppFakeUserIdReplacedEvent(contentMetadata, fakeUserId, realUserId))
