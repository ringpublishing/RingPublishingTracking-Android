/*
 *  Created by Daniel Całka on 7/23/24, 1:51 PM
 *  Copyright © 2024 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.internal.paid

internal enum class PaidEventParam(val text: String) {
    SUPPLIER_APP_ID("supplier_app_id"),
    PAYWALL_SUPPLIER("paywall_supplier"),
    PAYWALL_TEMPLATE_ID("paywall_template_id"),
    PAYWALL_VARIANT_ID("paywall_variant_id"),
    DISPLAY_MODE("display_mode"),
    SOURCE("source"),
    SOURCE_PUBLICATION_UUID("source_publication_uuid"),
    SOURCE_DX("source_dx"),
    CLOSURE_PERCENTAGE("closure_percentage"),
    TPCC("tpcc"),
    TERM_ID("term_id"),
    PAYMENT_METHOD("payment_method"),
    TERM_CONVERSION_ID("term_conversion_id"),
    METRIC_LIMIT_NAME("metric_limit_name"),
    FREE_PV_CNT("free_pv_cnt"),
    FREE_PV_LIMIT("free_pv_limit"),
    EVENT_DETAILS("event_details"),
    EVENT_CATEGORY("event_category"),
    EVENT_ACTION("event_action"),
}
