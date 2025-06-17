/*
 *  Created by Grzegorz Małopolski on 10/13/21, 5:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.timer.KeepAliveSendTimerCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Timer
import java.util.TimerTask

private const val ACTIVITY_TIMER_INTERVAL = 1000L // 1 second

class KeepAliveTimer(private val callback: KeepAliveSendTimerCallback)
{

	private val intervalsProvider = KeepAliveIntervalsProvider()

	private var trackingStartDate: Date? = null

	private val sendTimer = Timer("SendTimer")

	private var activityTimerJob: Job? = null

	private val backgroundPeriods = mutableListOf<TimePeriod>()
	private val pausedPeriods = mutableListOf<TimePeriod>()

	private val senderTasks = mutableListOf<TimerTask>()

    private var nextActivityTimerMillis: Long? = null

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

    fun scheduleActivityTimer() {
        activityTimerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                handleActivityTimer()
                handleEffectivePageViewTimer()
                delay(ACTIVITY_TIMER_INTERVAL)
            }
        }
    }

    private fun handleActivityTimer() {
        val nextTimer = nextActivityTimerMillis
        val timeFromStart = timeFromStartInForeground() ?: run {
            Logger.warn("KeepAliveTimer: handleActivityTimer() - timeFromStartInForeground is null")
            stopTimers()
            return
        }
        if (nextTimer == null || nextTimer <= timeFromStart) {
            nextTimer?.let {
                callback.onActivityTimer()
            }
            val interval = intervalsProvider.nextIntervalForActivityTrackingMillis(timeFromStart)
            Logger.debug("KeepAliveTimer: scheduleActivityTimer() Time: ${timeFromStart / 1000L} step: ${interval / 1000L} seconds")
            nextActivityTimerMillis = timeFromStart + interval
        }
    }

    private fun handleEffectivePageViewTimer() {
        callback.onEffectivePageViewTimer()
    }

    private fun stopTimers() {
        senderTasks.forEach { task -> task.cancel() }
        senderTasks.clear()

        activityTimerJob?.cancel()
        nextActivityTimerMillis = null
    }

    fun start() {
        trackingStartDate = Date()
        scheduleActivityTimer()
        scheduleSendTimer()
    }

    fun stop() {
        trackingStartDate = null
        stopTimers()
        backgroundPeriods.clear()
        pausedPeriods.clear()
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
