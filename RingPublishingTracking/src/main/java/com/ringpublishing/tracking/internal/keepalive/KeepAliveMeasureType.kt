/*
 *  Created by Grzegorz Małopolski on 10/18/21, 11:31 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

internal enum class KeepAliveMeasureType(val text: String)
{
	ACTIVITY_TIMER("T"),
	SEND_TIMER("S"),
	DOCUMENT_ALIVE("A"),
	DOCUMENT_INACTIVE("I"),
	ERROR("E"),
}
