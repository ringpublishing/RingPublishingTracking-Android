/*
 *  Created by Grzegorz Małopolski on 10/13/21, 5:34 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import com.ringpublishing.tracking.data.Event

class KeepAliveEventBuilder
{

	fun create(): Event
	{
		val event = Event()

		//todo: add parameters
		return event
	}
}