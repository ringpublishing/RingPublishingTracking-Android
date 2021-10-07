/*
 *  Created by Grzegorz Małopolski on 10/7/21, 4:37 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.data.EventName

interface EventFactory
{

	fun create(eventName: EventName): Event
}
