/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.paid.LikelihoodData
import com.ringpublishing.tracking.data.paid.MetricsData
import com.ringpublishing.tracking.data.paid.OfferContextData
import com.ringpublishing.tracking.data.paid.OfferData
import com.ringpublishing.tracking.data.paid.OfferDisplayMode
import com.ringpublishing.tracking.data.paid.PaymentMethod
import com.ringpublishing.tracking.data.paid.SubscriptionPaymentData
import com.ringpublishing.tracking.data.paid.SupplierData
import com.ringpublishing.tracking.internal.decorator.EventParam
import com.ringpublishing.tracking.internal.factory.PaidEventsFactory
import com.ringpublishing.tracking.internal.paid.PaidEventParam
import com.ringpublishing.tracking.internal.util.buildToDX
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL

class PaidEventsFactoryTest
{

    private val gson = GsonBuilder().create()

    private val sampleSupplierData = SupplierData(
        supplierAppId = "GTccriLYpe",
        paywallSupplier = "piano"
    )

    private val sampleOfferData = OfferData(
        supplierData = sampleSupplierData,
        paywallTemplateId = "OTT8ICJL3LWX",
        paywallVariantId = "OTVAEW37T5NG3",
        displayMode = OfferDisplayMode.INLINE
    )

    private val sampleOfferContextData = OfferContextData(
        source = "closedArticle",
        closurePercentage = 50
    )

    private val sampleSubscriptionPaymentData = SubscriptionPaymentData(
        subscriptionBasePrice = 100.0f,
        subscriptionPromoPrice = 99.99f,
        subscriptionPromoDuration = "1w",
        subscriptionPriceCurrency = "usd",
        paymentMethod = PaymentMethod.GOOGLE_PLAY
    )

    private val sampleMetricsData = MetricsData(
        metricLimitName = "OnetMeter",
        freePageViewCount = 9,
        freePageViewLimit = 10
    )

    private val sampleLikelihoodData = LikelihoodData(
        likelihoodToSubscribe = 5,
        likelihoodToCancel = 4
    )

    private val sampleContentMetadata = ContentMetadata(
        publicationId = "publicationId",
        publicationUrl = URL("https://domain.com"),
        sourceSystemName = "source System_Name",
        contentPartIndex = 1,
        paidContent = true,
        contentId = "my-unique-content-id-1234"
    )

    @Before
    fun `Bypass android_util_Base64 to java_util_Base64`()
    {
        mockkStatic(Base64::class)
        val arraySlot = slot<ByteArray>()

        every {
            Base64.encodeToString(capture(arraySlot), Base64.NO_WRAP)
        } answers {
            java.util.Base64.getEncoder().encodeToString(arraySlot.captured)
        }
    }

    @Test
    fun createPaidEvent_StringParameters_ThenParametersInEvent()
    {
        val eventsFactory = PaidEventsFactory(gson)
        val event = eventsFactory.createShowOfferEvent(
            contentMetadata = sampleContentMetadata,
            offerData = sampleOfferData,
            offerContextData = sampleOfferContextData,
            targetPromotionCampaignCode = "hard_xmass_promoInline"
        )

        Assert.assertTrue(event.parameters.isNotEmpty())
    }

    @Test
    fun createShowOfferEvent_ThenProperParametersInEvent()
    {
        val eventsFactory = PaidEventsFactory(gson)
        val sampleTpcc = "hard_xmass_promoInline"
        val event = eventsFactory.createShowOfferEvent(
            contentMetadata = sampleContentMetadata,
            offerData = sampleOfferData,
            offerContextData = sampleOfferContextData,
            targetPromotionCampaignCode = sampleTpcc
        )

        Assert.assertTrue(event.parameters.isNotEmpty())
        Assert.assertEquals(event.parameters[PaidEventParam.SUPPLIER_APP_ID.text], sampleOfferData.supplierData.supplierAppId)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_SUPPLIER.text], sampleOfferData.supplierData.paywallSupplier)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_TEMPLATE_ID.text], sampleOfferData.paywallTemplateId)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_VARIANT_ID.text], sampleOfferData.paywallVariantId)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE.text], sampleOfferContextData.source)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_PUBLICATION_UUID.text], sampleContentMetadata.publicationId)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_DX.text], sampleContentMetadata.buildToDX())
        Assert.assertEquals(event.parameters[PaidEventParam.CLOSURE_PERCENTAGE.text], sampleOfferContextData.closurePercentage)
        Assert.assertEquals(event.parameters[PaidEventParam.TPCC.text], sampleTpcc)
        Assert.assertEquals(event.parameters[EventParam.MARKED_AS_PAID_DATA.text], mockRdlcnEncodingPaid())
    }

    @Test
    fun createShowOfferTeaserEvent_ThenProperParametersInEvent()
    {
        val eventsFactory = PaidEventsFactory(gson)
        val sampleTpcc = "hard_xmass_promoInline"
        val event = eventsFactory.createShowOfferTeaserEvent(
            contentMetadata = sampleContentMetadata,
            offerData = sampleOfferData,
            offerContextData = sampleOfferContextData,
            targetPromotionCampaignCode = sampleTpcc
        )

        Assert.assertTrue(event.parameters.isNotEmpty())
        Assert.assertEquals(event.parameters[PaidEventParam.SUPPLIER_APP_ID.text], sampleOfferData.supplierData.supplierAppId)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_SUPPLIER.text], sampleOfferData.supplierData.paywallSupplier)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_TEMPLATE_ID.text], sampleOfferData.paywallTemplateId)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_VARIANT_ID.text], sampleOfferData.paywallVariantId)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE.text], sampleOfferContextData.source)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_PUBLICATION_UUID.text], sampleContentMetadata.publicationId)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_DX.text], sampleContentMetadata.buildToDX())
        Assert.assertEquals(event.parameters[PaidEventParam.CLOSURE_PERCENTAGE.text], sampleOfferContextData.closurePercentage)
        Assert.assertEquals(event.parameters[PaidEventParam.TPCC.text], sampleTpcc)
        Assert.assertEquals(event.parameters[EventParam.MARKED_AS_PAID_DATA.text], mockRdlcnEncodingPaid())
    }

    @Test
    fun createPurchaseClickButtonEvent_ThenProperParametersInEvent()
    {
        val eventsFactory = PaidEventsFactory(gson)
        val sampleTpcc = "hard_xmass_promoInline"
        val sampleTermId = "TMEVT00KVHV0"
        val event = eventsFactory.createPurchaseClickButtonEvent(
            contentMetadata = sampleContentMetadata,
            offerData = sampleOfferData,
            offerContextData = sampleOfferContextData.copy(closurePercentage = null),
            termId = sampleTermId,
            targetPromotionCampaignCode = sampleTpcc
        )

        Assert.assertTrue(event.parameters.isNotEmpty())
        Assert.assertEquals(event.parameters[PaidEventParam.SUPPLIER_APP_ID.text], sampleOfferData.supplierData.supplierAppId)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_SUPPLIER.text], sampleOfferData.supplierData.paywallSupplier)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_TEMPLATE_ID.text], sampleOfferData.paywallTemplateId)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_VARIANT_ID.text], sampleOfferData.paywallVariantId)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE.text], sampleOfferContextData.source)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_PUBLICATION_UUID.text], sampleContentMetadata.publicationId)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_DX.text], sampleContentMetadata.buildToDX())
        Assert.assertEquals(event.parameters[PaidEventParam.CLOSURE_PERCENTAGE.text], null)
        Assert.assertEquals(event.parameters[PaidEventParam.TPCC.text], sampleTpcc)
        Assert.assertEquals(event.parameters[PaidEventParam.TERM_ID.text], sampleTermId)
        Assert.assertEquals(event.parameters[EventParam.MARKED_AS_PAID_DATA.text], mockRdlcnEncodingPaid())
    }

    @Test
    fun createPurchaseEvent_ThenProperParametersInEvent()
    {
        val eventsFactory = PaidEventsFactory(gson)
        val sampleTpcc = "hard_xmass_promoInline"
        val sampleTermId = "TMEVT00KVHV0"
        val sampleFakeUserId = "fake_001"
        val sampleTermConversionId = "TCCJTS9X87VB"
        val sampleFakePurchaseJson =
            "{\"subscription_base_price\":100.0,\"subscription_promo_price\":99.99,\"subscription_promo_duration\":\"1w\",\"subscription_price_currency\":\"usd\",\"fake_user_id\":\"fake_001\"}"
        val event = eventsFactory.createPurchaseEvent(
            contentMetadata = sampleContentMetadata,
            offerData = sampleOfferData,
            offerContextData = sampleOfferContextData.copy(closurePercentage = null),
            subscriptionPaymentData = sampleSubscriptionPaymentData,
            termId = sampleTermId,
            termConversionId = sampleTermConversionId,
            targetPromotionCampaignCode = sampleTpcc,
            fakeUserId = sampleFakeUserId
        )

        Assert.assertTrue(event.parameters.isNotEmpty())
        Assert.assertEquals(event.parameters[PaidEventParam.SUPPLIER_APP_ID.text], sampleOfferData.supplierData.supplierAppId)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_SUPPLIER.text], sampleOfferData.supplierData.paywallSupplier)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_TEMPLATE_ID.text], sampleOfferData.paywallTemplateId)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_VARIANT_ID.text], sampleOfferData.paywallVariantId)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE.text], sampleOfferContextData.source)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_PUBLICATION_UUID.text], sampleContentMetadata.publicationId)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_DX.text], sampleContentMetadata.buildToDX())
        Assert.assertEquals(event.parameters[PaidEventParam.CLOSURE_PERCENTAGE.text], null)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYMENT_METHOD.text], sampleSubscriptionPaymentData.paymentMethod.text)
        Assert.assertEquals(event.parameters[PaidEventParam.TPCC.text], sampleTpcc)
        Assert.assertEquals(event.parameters[PaidEventParam.TERM_ID.text], sampleTermId)
        Assert.assertEquals(event.parameters[PaidEventParam.TERM_CONVERSION_ID.text], sampleTermConversionId)
        Assert.assertEquals(event.parameters[PaidEventParam.EVENT_DETAILS.text], sampleFakePurchaseJson)
        Assert.assertEquals(event.parameters[EventParam.MARKED_AS_PAID_DATA.text], mockRdlcnEncodingPaid())
    }

    @Test
    fun createShowMetricLimitEvent_ThenProperParametersInEvent()
    {
        val eventsFactory = PaidEventsFactory(gson)
        val event = eventsFactory.createShowMetricLimitEvent(
            contentMetadata = sampleContentMetadata,
            metricsData = sampleMetricsData,
            supplierData = sampleSupplierData,
        )

        Assert.assertTrue(event.parameters.isNotEmpty())
        Assert.assertEquals(event.parameters[PaidEventParam.METRIC_LIMIT_NAME.text], sampleMetricsData.metricLimitName)
        Assert.assertEquals(event.parameters[PaidEventParam.FREE_PV_CNT.text], sampleMetricsData.freePageViewCount)
        Assert.assertEquals(event.parameters[PaidEventParam.FREE_PV_LIMIT.text], sampleMetricsData.freePageViewLimit)
        Assert.assertEquals(event.parameters[PaidEventParam.SUPPLIER_APP_ID.text], sampleSupplierData.supplierAppId)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_SUPPLIER.text], sampleSupplierData.paywallSupplier)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_PUBLICATION_UUID.text], sampleContentMetadata.publicationId)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_DX.text], sampleContentMetadata.buildToDX())
        Assert.assertEquals(event.parameters[EventParam.MARKED_AS_PAID_DATA.text], mockRdlcnEncodingPaid())
    }

    @Test
    fun createLikelihoodScoringEvent_ThenProperParametersInEvent()
    {
        val eventsFactory = PaidEventsFactory(gson)
        val sampleLikelihoodJson = "{\"lts\":${sampleLikelihoodData.likelihoodToSubscribe},\"ltc\":${sampleLikelihoodData.likelihoodToCancel}}"
        val event = eventsFactory.createLikelihoodScoringEvent(
            contentMetadata = sampleContentMetadata,
            supplierData = sampleSupplierData,
            likelihoodData = sampleLikelihoodData
        )

        Assert.assertTrue(event.parameters.isNotEmpty())
        Assert.assertEquals(event.parameters[PaidEventParam.SUPPLIER_APP_ID.text], sampleSupplierData.supplierAppId)
        Assert.assertEquals(event.parameters[PaidEventParam.PAYWALL_SUPPLIER.text], sampleSupplierData.paywallSupplier)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_PUBLICATION_UUID.text], sampleContentMetadata.publicationId)
        Assert.assertEquals(event.parameters[PaidEventParam.SOURCE_DX.text], sampleContentMetadata.buildToDX())
        Assert.assertEquals(event.parameters[PaidEventParam.EVENT_DETAILS.text], sampleLikelihoodJson)
        Assert.assertEquals(event.parameters[EventParam.MARKED_AS_PAID_DATA.text], mockRdlcnEncodingPaid())
    }

    @Test
    fun createMobileAppFakeUserIdReplacedEvent_ThenProperParametersInEvent()
    {
        val eventsFactory = PaidEventsFactory(gson)
        val sampleFakeUserId = "fake_001"
        val sampleRealUserId = "real_001"
        val sampleUserJson = "{\"fake_user_id\":\"${sampleFakeUserId}\",\"real_user_id\":\"${sampleRealUserId}\"}"
        val event = eventsFactory.createMobileAppFakeUserIdReplacedEvent(
            temporaryUserId = sampleFakeUserId,
            realUserId = sampleRealUserId
        )

        Assert.assertTrue(event.parameters.isNotEmpty())
        Assert.assertEquals(event.parameters[PaidEventParam.EVENT_DETAILS.text], sampleUserJson)
    }

    private fun mockRdlcnEncodingPaid() = encode(
        "{\"publication\":{\"premium\":${sampleContentMetadata.paidContent}},\"source\":{\"id\":\"${sampleContentMetadata.contentId}\"" +
                ",\"system\":\"${sampleContentMetadata.sourceSystemName}\"}}"
    )

    private fun encode(input: String): String {
        return Base64.encodeToString(
            input.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
    }
}
