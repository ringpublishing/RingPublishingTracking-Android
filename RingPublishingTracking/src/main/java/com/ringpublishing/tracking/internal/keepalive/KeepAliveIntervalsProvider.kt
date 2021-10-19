/*
 *  Created by Grzegorz Małopolski on 10/18/21, 12:08 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

class KeepAliveIntervalsProvider
{
	private val milliseconds = 1000L

	private val steps = listOf(1, 2, 3, 4, 5, 6, 8, 10, 12, 14, 17)

	private val pseudoFibonacci = listOf(5, 10, 20, 30, 60, 100, 140, 184)

	fun nextIntervalForActivityTrackingMillis(timeInMillis: Long): Long
	{
		val delaySeconds = when(val timeInSeconds = timeInMillis / milliseconds)
		{
			in 0L..14L -> steps.first { it > timeInSeconds } - timeInSeconds
			in 15L..46L -> 3L
			else -> 8L
		}
		return delaySeconds * milliseconds
	}

	fun nextIntervalForSendingMillis(timeInMillis: Long): Long
	{
		val delaySeconds = when(val timeInSeconds = timeInMillis / milliseconds)
		{
			in 0L..183L -> pseudoFibonacci.first { it > timeInSeconds } - timeInSeconds
			in 183L..899L -> 60L
			else -> 300L
		}

		return delaySeconds * milliseconds
	}
}
