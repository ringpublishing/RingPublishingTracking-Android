/*
 *  Created by Grzegorz Małopolski on 9/21/21, 4:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data

import java.net.URL

/**
 * Content metadata
 *
 * @param publicationId: Publication identifier in source system (CMS)
 * @param publicationUrl: Website url address for given publication
 * @param sourceSystemName: Source system (CMS) name
 * @param contentPartIndex: Index of displayed content part (applies only if given content can be consumed in parts).
 * @param paidContent: Is content marked on CMS as paid
 * @param contentId: Content identifier in source system (CMS)
 */
data class ContentMetadata(
    val publicationId: String,
    val publicationUrl: URL,
    val sourceSystemName: String,
    val contentPartIndex: Int = 1,
    val paidContent: Boolean,
    val contentId: String,
)
