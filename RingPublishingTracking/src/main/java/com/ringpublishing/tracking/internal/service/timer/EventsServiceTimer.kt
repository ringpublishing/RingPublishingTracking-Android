package com.ringpublishing.tracking.internal.service.timer

import com.ringpublishing.tracking.internal.log.Logger
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.TimerTask

internal class EventsServiceTimer
{

    var postInterval = 0L
    var flushCallback: EventServiceTimerCallback? = null

    private val timer = Timer("EventsServiceTimer")
    private var lastFlushDate: Date? = null
    private var flushScheduled = false

    fun scheduleFlush()
    {
        if (flushScheduled) return

        if (isMinimumInterval())
        {
            readyToFlush()
        } else {
            scheduleTimer()
        }
    }

    private fun scheduleTimer()
    {
        Logger.debug("EventsServiceTimer scheduleTimer time: $postInterval")
        flushScheduled = true

        timer.schedule(
            object : TimerTask()
            {
                override fun run()
                {
                    readyToFlush()
                }
            },
            postInterval
        )
    }

    private fun readyToFlush()
    {
        Logger.debug("EventsServiceTimer readyToFlush")
        flushCallback?.readyToFlush()
        lastFlushDate = Date()
        flushScheduled = false
    }

    private fun isMinimumInterval(): Boolean
    {
        val lastDate = lastFlushDate ?: return true

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = lastDate.time + postInterval
        return calendar.time.before(Date())
    }
}
