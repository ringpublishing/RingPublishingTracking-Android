/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data.paid

/**
 * Data regarding the supplier of sales offers and offers themselves
 *
 * @param [supplierData]: data regarding the supplier of sales
 * @param [paywallTemplateId]: offer template id
 * @param [paywallVariantId]: offer template variant id
 * @param [displayMode]: offer display mode (inline or modal)
 *
 * @see SupplierData
 * @see OfferDisplayMode
 */
data class OfferData(
    val supplierData: SupplierData,
    val paywallTemplateId: String,
    val paywallVariantId: String?,
    val displayMode: OfferDisplayMode,
)

/**
 * Data regarding the supplier of sales
 *
 * @param [supplierAppId]: offer supplier application id
 * @param [paywallSupplier]: offer supplier name
 */
data class SupplierData(
    val supplierAppId: String,
    val paywallSupplier: String,
)

/**
 * Offer display mode (inline or modal)
 *
 * @param [text]: label
 */
enum class OfferDisplayMode(val text: String) {
    INLINE("inline"),
    MODEL("modal"),
}
