/*
 *  Created by Grzegorz Małopolski on 10/29/21, 12:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import org.junit.Assert
import org.junit.Test

class KeepAliveIntervalsProviderTest
{

	private val keepAliveIntervalsProvider = KeepAliveIntervalsProvider()

	@Test
	fun nextIntervalForActivityTrackingMillis_WhenTImeSet_ThenExpectedResults()
	{
		Assert.assertEquals(1000, keepAliveIntervalsProvider.nextIntervalForActivityTrackingMillis(1000))
		Assert.assertEquals(3000, keepAliveIntervalsProvider.nextIntervalForActivityTrackingMillis(16000))
		Assert.assertEquals(8000, keepAliveIntervalsProvider.nextIntervalForActivityTrackingMillis(50000))
	}

	@Test
	fun nextIntervalForSendingMillis()
	{
		Assert.assertEquals(4000, keepAliveIntervalsProvider.nextIntervalForSendingMillis(1000))
		Assert.assertEquals(3000, keepAliveIntervalsProvider.nextIntervalForSendingMillis(2000))
	}
}
