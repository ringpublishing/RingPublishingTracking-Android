/*
 *  Created by Grzegorz Małopolski on 9/23/21, 2:24 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.data

import java.net.URL
import java.util.UUID

data class SampleArticle(
    val title: String,
    val content: String,
    val publicationUrl: URL,
    val contentWasPaidFor: Boolean,
    val contentId: String
)
{
    val publicationId = UUID.randomUUID().toString()
    val sourceSystemName: String = "My Awesome CMS"
}
