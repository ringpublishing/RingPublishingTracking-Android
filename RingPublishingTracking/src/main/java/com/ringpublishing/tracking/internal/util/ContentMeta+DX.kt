/*
 *  Created by Grzegorz Małopolski on 10/15/21, 1:42 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.util

import com.ringpublishing.tracking.data.ContentMetadata

internal fun ContentMetadata.buildToDX(): String
{
	val paid = if (paidContent) "t" else "f"
	return "PV_4,${sourceSystemName.trim().replace(" ", "_")},${publicationId.trim()},$contentPartIndex,$paid"
}
