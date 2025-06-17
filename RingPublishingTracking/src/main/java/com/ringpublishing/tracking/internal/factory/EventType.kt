/*
 *  Created by Grzegorz Małopolski on 10/11/21, 1:28 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

enum class EventType(val text: String)
{
	CLICK("ClkEvent"),
	USER_ACTION("UserAction"),
	PAGE_VIEW("PageView"),
	KEEP_ALIVE("KeepAlive"),
	VIDEO("VidEvent"),
	ERROR("ErrEvent"),
	PAID("PaidEvent"),
	POLARIS("PolarisEvent"),
}
