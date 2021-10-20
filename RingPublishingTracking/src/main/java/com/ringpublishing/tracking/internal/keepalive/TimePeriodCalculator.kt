/*
 *  Created by Grzegorz Małopolski on 10/20/21, 9:34 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import java.util.Date

object TimePeriodCalculator
{

	fun sumTimePeriods(periods: List<TimePeriod>) = periods.map {
		val start = it.startDate
		val end = it.endDate

		if (end != null) { end.time - start.time } else { Date().time - start.time }
	}.sum()
}
