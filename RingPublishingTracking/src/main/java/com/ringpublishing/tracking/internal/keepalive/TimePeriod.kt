/*
 *  Created by Grzegorz Małopolski on 10/19/21, 10:29 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import java.util.Date

data class TimePeriod(val startDate: Date, var endDate: Date? = null)
{
	fun end()
	{
		endDate = Date()
	}
}
