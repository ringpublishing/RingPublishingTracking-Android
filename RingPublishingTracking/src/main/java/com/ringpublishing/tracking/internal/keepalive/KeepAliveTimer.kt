/*
 *  Created by Grzegorz Małopolski on 10/13/21, 5:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.timer.KeepAliveSendTimerCallback
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class KeepAliveTimer(private val callback: KeepAliveSendTimerCallback)
{

	private val intervalsProvider = KeepAliveIntervalsProvider()

	private var trackingStartDate: Date? = null

	private val sendTimer = Timer("SendTimer")
	private val activityTimer = Timer("ActivityTimer")

	private val backgroundPeriods = mutableListOf<TimePeriod>()
	private val pausedPeriods = mutableListOf<TimePeriod>()

	private val activityTasks = mutableListOf<TimerTask>()
	private val senderTasks = mutableListOf<TimerTask>()

	fun start()
	{
		trackingStartDate = Date()
		scheduleActivityTimer()
		scheduleSendTimer()
	}

	fun stop()
	{
		trackingStartDate = null
		stopTimers()
		backgroundPeriods.clear()
		pausedPeriods.clear()
	}

	fun scheduleSendTimer()
	{
		if (senderTasks.isNotEmpty()) return

		val timeFromStart = timeFromStartInForeground()

		if (timeFromStart == null)
		{
			stopTimers()
			return
		}

		val stepMillis = intervalsProvider.nextIntervalForSendingMillis(timeFromStart)

		val task = object : TimerTask()
		{
			override fun run()
			{
				senderTasks.remove(this)
				callback.onSendTimer()
			}
		}

		Logger.debug("KeepAliveTimer: scheduleSendTimer() Time ${timeFromStart / 1000L} step:${stepMillis / 1000L} seconds")
		senderTasks.add(task)
		sendTimer.schedule(task, stepMillis)
	}

	fun scheduleActivityTimer()
	{
		if (activityTasks.isNotEmpty()) return

		val timeFromStart = timeFromStartInForeground()

		if (timeFromStart == null)
		{
			stopTimers()
			return
		}

		val postIntervalMillis = intervalsProvider.nextIntervalForActivityTrackingMillis(timeFromStart)

		val task = object : TimerTask()
		{
			override fun run()
			{
				activityTasks.remove(this)
				callback.onActivityTimer()
			}
		}

		Logger.debug("KeepAliveTimer: scheduleActivityTimer() Time: ${timeFromStart / 1000L} step: ${postIntervalMillis / 1000L} seconds")

		activityTasks.add(task)
		activityTimer.schedule(task, postIntervalMillis)
	}

	private fun stopTimers()
	{
		activityTasks.forEach { task -> task.cancel() }
		activityTasks.clear()

		senderTasks.forEach { task -> task.cancel() }
		senderTasks.clear()
	}

	fun pause()
	{
		pausedPeriods.add(TimePeriod(Date()))
		stopTimers()
	}

	fun resume()
	{
		pausedPeriods.lastOrNull()?.end()
	}

	fun pauseBackground()
	{
		backgroundPeriods.add(TimePeriod(Date()))
		stopTimers()
	}

	fun resumeBackground()
	{
		backgroundPeriods.lastOrNull()?.end()
	}

	fun timeFromStartInForeground(): Long?
	{
		val startDate = trackingStartDate ?: return null

		val backgroundTime = TimePeriodCalculator.sumTimePeriods(backgroundPeriods)
		val pausedTime = TimePeriodCalculator.sumTimePeriods(pausedPeriods)
		val startTimeToNow = Date().time - startDate.time

		return startTimeToNow - backgroundTime - pausedTime
	}
}
