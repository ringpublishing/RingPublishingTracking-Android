/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.internal.factory

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.paid.LikelihoodData
import com.ringpublishing.tracking.data.paid.MetricsData
import com.ringpublishing.tracking.data.paid.OfferContextData
import com.ringpublishing.tracking.data.paid.OfferData
import com.ringpublishing.tracking.data.paid.SubscriptionPaymentData
import com.ringpublishing.tracking.data.paid.SupplierData
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.decorator.EventParam
import com.ringpublishing.tracking.internal.decorator.createMarkedAsPaidParam
import com.ringpublishing.tracking.internal.paid.PaidEventParam
import com.ringpublishing.tracking.internal.util.buildToDX

internal class PaidEventsFactory(private val gson: Gson) {

    fun createShowOfferEvent(contentMetadata: ContentMetadata, offerData: OfferData, offerContextData: OfferContextData, targetPromotionCampaignCode: String?): Event {
        val parameters = mutableMapOf<String, Any>().apply {
            this[PaidEventParam.EVENT_CATEGORY.text] = "checkout"
            this[PaidEventParam.EVENT_ACTION.text] = "showOffer"
            this[PaidEventParam.SUPPLIER_APP_ID.text] = offerData.supplierData.supplierAppId
            this[PaidEventParam.PAYWALL_SUPPLIER.text] = offerData.supplierData.paywallSupplier
            this[PaidEventParam.PAYWALL_TEMPLATE_ID.text] = offerData.paywallTemplateId
            this[PaidEventParam.DISPLAY_MODE.text] = offerData.displayMode.text
            this[PaidEventParam.SOURCE.text] = offerContextData.source
            this[PaidEventParam.SOURCE_DX.text] = contentMetadata.buildToDX()
            this[PaidEventParam.SOURCE_PUBLICATION_UUID.text] = contentMetadata.publicationId

            offerData.paywallVariantId?.let {
                this[PaidEventParam.PAYWALL_VARIANT_ID.text] = it
            }
            offerContextData.closurePercentage?.let {
                this[PaidEventParam.CLOSURE_PERCENTAGE.text] = it
            }
            targetPromotionCampaignCode?.let {
                this[PaidEventParam.TPCC.text] = it
            }
            createMarkedAsPaidParam(gson, contentMetadata)?.let { param -> this[EventParam.MARKED_AS_PAID_DATA.text] = param }
        }

        return createPaidEvent(parameters)
    }

    fun createShowOfferTeaserEvent(contentMetadata: ContentMetadata, offerData: OfferData, offerContextData: OfferContextData, targetPromotionCampaignCode: String?): Event
    {
        val parameters = mutableMapOf<String, Any>().apply {
            this[PaidEventParam.EVENT_CATEGORY.text] = "checkout"
            this[PaidEventParam.EVENT_ACTION.text] = "showOfferTeaser"
            this[PaidEventParam.SUPPLIER_APP_ID.text] = offerData.supplierData.supplierAppId
            this[PaidEventParam.PAYWALL_SUPPLIER.text] = offerData.supplierData.paywallSupplier
            this[PaidEventParam.PAYWALL_TEMPLATE_ID.text] = offerData.paywallTemplateId
            this[PaidEventParam.DISPLAY_MODE.text] = offerData.displayMode.text
            this[PaidEventParam.SOURCE.text] = offerContextData.source
            this[PaidEventParam.SOURCE_DX.text] = contentMetadata.buildToDX()
            this[PaidEventParam.SOURCE_PUBLICATION_UUID.text] = contentMetadata.publicationId

            offerData.paywallVariantId?.let {
                this[PaidEventParam.PAYWALL_VARIANT_ID.text] = it
            }
            offerContextData.closurePercentage?.let {
                this[PaidEventParam.CLOSURE_PERCENTAGE.text] = it
            }
            targetPromotionCampaignCode?.let {
                this[PaidEventParam.TPCC.text] = it
            }
            createMarkedAsPaidParam(gson, contentMetadata)?.let { param -> this[EventParam.MARKED_AS_PAID_DATA.text] = param }
        }

        return createPaidEvent(parameters)
    }

    fun createPurchaseClickButtonEvent(
        contentMetadata: ContentMetadata,
        offerData: OfferData,
        offerContextData: OfferContextData,
        termId: String,
        targetPromotionCampaignCode: String?
    ): Event
    {
        val parameters = mutableMapOf<String, Any>().apply {
            this[PaidEventParam.EVENT_CATEGORY.text] = "checkout"
            this[PaidEventParam.EVENT_ACTION.text] = "clickButton"
            this[PaidEventParam.SUPPLIER_APP_ID.text] = offerData.supplierData.supplierAppId
            this[PaidEventParam.PAYWALL_SUPPLIER.text] = offerData.supplierData.paywallSupplier
            this[PaidEventParam.PAYWALL_TEMPLATE_ID.text] = offerData.paywallTemplateId
            this[PaidEventParam.DISPLAY_MODE.text] = offerData.displayMode.text
            this[PaidEventParam.SOURCE.text] = offerContextData.source
            this[PaidEventParam.TERM_ID.text] = termId
            this[PaidEventParam.SOURCE_DX.text] = contentMetadata.buildToDX()
            this[PaidEventParam.SOURCE_PUBLICATION_UUID.text] = contentMetadata.publicationId

            offerData.paywallVariantId?.let {
                this[PaidEventParam.PAYWALL_VARIANT_ID.text] = it
            }
            targetPromotionCampaignCode?.let {
                this[PaidEventParam.TPCC.text] = it
            }
            createMarkedAsPaidParam(gson, contentMetadata)?.let { param -> this[EventParam.MARKED_AS_PAID_DATA.text] = param }
        }

        return createPaidEvent(parameters)
    }

    fun createPurchaseEvent(
        contentMetadata: ContentMetadata,
        offerData: OfferData,
        offerContextData: OfferContextData,
        subscriptionPaymentData: SubscriptionPaymentData,
        termId: String,
        termConversionId: String,
        targetPromotionCampaignCode: String?,
        fakeUserId: String?
    ): Event
    {
        val parameters = mutableMapOf<String, Any>().apply {
            this[PaidEventParam.EVENT_CATEGORY.text] = "checkout"
            this[PaidEventParam.EVENT_ACTION.text] = "purchase"
            this[PaidEventParam.SUPPLIER_APP_ID.text] = offerData.supplierData.supplierAppId
            this[PaidEventParam.PAYWALL_SUPPLIER.text] = offerData.supplierData.paywallSupplier
            this[PaidEventParam.PAYWALL_TEMPLATE_ID.text] = offerData.paywallTemplateId
            this[PaidEventParam.DISPLAY_MODE.text] = offerData.displayMode.text
            this[PaidEventParam.SOURCE.text] = offerContextData.source
            this[PaidEventParam.TERM_ID.text] = termId
            this[PaidEventParam.TERM_CONVERSION_ID.text] = termConversionId
            this[PaidEventParam.PAYMENT_METHOD.text] = subscriptionPaymentData.paymentMethod.text
            this[PaidEventParam.SUBSCRIPTION_BASE_PRICE.text] = subscriptionPaymentData.subscriptionBasePrice
            this[PaidEventParam.SUBSCRIPTION_PRICE_CURRENCY.text] = subscriptionPaymentData.subscriptionPriceCurrency
            this[PaidEventParam.SOURCE_DX.text] = contentMetadata.buildToDX()
            this[PaidEventParam.SOURCE_PUBLICATION_UUID.text] = contentMetadata.publicationId

            offerData.paywallVariantId?.let {
                this[PaidEventParam.PAYWALL_VARIANT_ID.text] = it
            }
            targetPromotionCampaignCode?.let {
                this[PaidEventParam.TPCC.text] = it
            }
            subscriptionPaymentData.subscriptionPromoPrice?.let {
                this[PaidEventParam.SUBSCRIPTION_PROMO_PRICE.text] = it
            }
            subscriptionPaymentData.subscriptionPromoPriceDuration?.let {
                this[PaidEventParam.SUBSCRIPTION_PROMO_PRICE_DURATION.text] = it
            }
            createUserIdJson(UserId(fakeUserId = fakeUserId))?.let {
                this[PaidEventParam.EVENT_DETAILS.text] = it
            }
            createMarkedAsPaidParam(gson, contentMetadata)?.let { param -> this[EventParam.MARKED_AS_PAID_DATA.text] = param }
        }

        return createPaidEvent(parameters)
    }

    fun createShowMetricLimitEvent(contentMetadata: ContentMetadata, supplierData: SupplierData, metricsData: MetricsData): Event
    {
        val parameters = mutableMapOf<String, Any>().apply {
            this[PaidEventParam.EVENT_CATEGORY.text] = "metric_limit"
            this[PaidEventParam.EVENT_ACTION.text] = "showMetricLimit"
            this[PaidEventParam.SUPPLIER_APP_ID.text] = supplierData.supplierAppId
            this[PaidEventParam.PAYWALL_SUPPLIER.text] = supplierData.paywallSupplier
            this[PaidEventParam.METRIC_LIMIT_NAME.text] = metricsData.metricLimitName
            this[PaidEventParam.FREE_PV_CNT.text] = metricsData.freePageViewCount
            this[PaidEventParam.FREE_PV_LIMIT.text] = metricsData.freePageViewLimit
            this[PaidEventParam.SOURCE_PUBLICATION_UUID.text] = contentMetadata.publicationId
            this[PaidEventParam.SOURCE_DX.text] = contentMetadata.buildToDX()
            createMarkedAsPaidParam(gson, contentMetadata)?.let { param -> this[EventParam.MARKED_AS_PAID_DATA.text] = param }
        }

        return createPaidEvent(parameters)
    }

    fun createLikelihoodScoringEvent(
        contentMetadata: ContentMetadata,
        supplierData: SupplierData,
        likelihoodData: LikelihoodData
    ): Event
    {
        val parameters = mutableMapOf<String, Any>().apply {
            this[PaidEventParam.EVENT_CATEGORY.text] = "likelihood_scoring"
            this[PaidEventParam.EVENT_ACTION.text] = "likelihoodScoring"
            this[PaidEventParam.SUPPLIER_APP_ID.text] = supplierData.supplierAppId
            this[PaidEventParam.PAYWALL_SUPPLIER.text] = supplierData.paywallSupplier
            this[PaidEventParam.SOURCE_PUBLICATION_UUID.text] = contentMetadata.publicationId
            this[PaidEventParam.SOURCE_DX.text] = contentMetadata.buildToDX()
            createLikelihoodDataJson(likelihoodData)?.let {
                this[PaidEventParam.EVENT_DETAILS.text] = it
            }
            createMarkedAsPaidParam(gson, contentMetadata)?.let { param -> this[EventParam.MARKED_AS_PAID_DATA.text] = param }
        }

        return createPaidEvent(parameters)
    }

    fun createMobileAppFakeUserIdReplacedEvent(contentMetadata: ContentMetadata, temporaryUserId: String, realUserId: String): Event
    {
        val parameters = mutableMapOf<String, Any>().apply {
            this[PaidEventParam.EVENT_CATEGORY.text] = "mobile_app_fake_user_id_replaced"
            this[PaidEventParam.EVENT_ACTION.text] = "mobileAppFakeUserIdReplaced"
            createUserIdJson(UserId(temporaryUserId, realUserId))?.let {
                this[PaidEventParam.EVENT_DETAILS.text] = it
            }
            createMarkedAsPaidParam(gson, contentMetadata)?.let { param -> this[EventParam.MARKED_AS_PAID_DATA.text] = param }
        }

        return createPaidEvent(parameters)
    }

    private fun createPaidEvent(parameters: MutableMap<String, Any>) = Event(
        analyticsSystemName = AnalyticsSystem.KROPKA_EVENTS.text,
        name = EventType.PAID.text,
        parameters = parameters
    )

    private fun createUserIdJson(userId: UserId) = runCatching {
        gson.toJson(userId)
    }.getOrNull()

    private fun createLikelihoodDataJson(likelihoodData: LikelihoodData) = runCatching {
        gson.toJson(likelihoodData)
    }.getOrNull()

    private class UserId(
        @SerializedName("fake_user_id") val fakeUserId: String? = null,
        @SerializedName("real_user_id") val realUserId: String? = null
    )
}