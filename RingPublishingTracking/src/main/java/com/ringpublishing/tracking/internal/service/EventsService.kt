package com.ringpublishing.tracking.internal.service

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.queue.EventsQueue
import com.ringpublishing.tracking.internal.service.result.ReportEventStatus
import com.ringpublishing.tracking.internal.service.timer.EventServiceTimerCallback
import com.ringpublishing.tracking.internal.service.timer.EventsServiceTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * This service will add events and will send it when needed
 * Include timer, queue and can flush events
 */
internal class EventsService(
    private val apiService: ApiService,
    private val eventsQueue: EventsQueue,
    private val eventsServiceTimer: EventsServiceTimer,
    private val configurationManager: ConfigurationManager
) : EventServiceTimerCallback
{

    init
    {
        eventsServiceTimer.flushCallback = this
    }

	@Synchronized
    fun addEvent(event: Event)
    {
        if (configurationManager.isSendEventsBlocked())
        {
            Logger.info("EventsService: OptOut mode enabled. Ignore new event")
            return
        }

        Logger.info("EventsService: Added new event to queue: $event")
        eventsQueue.add(event)
        eventsServiceTimer.scheduleFlush()
    }

	@Synchronized
    fun addEvents(events: List<Event>)
    {
        if (configurationManager.isSendEventsBlocked())
        {
            Logger.info("EventsService: OptOut mode enabled. Ignore new events")
            return
        }

        Logger.info("EventsService: Added new events to queue: $events")
        eventsQueue.addAll(events)
        eventsServiceTimer.scheduleFlush()
    }

	@Synchronized
	private fun flush()
	{
		CoroutineScope(SupervisorJob() + Dispatchers.IO).launch(Dispatchers.IO) {

			val eventsToSend = eventsQueue.getMaximumEventsToSend()

			Logger.debug("EventsService: Flush ${eventsToSend.size} events $eventsToSend")

			if (eventsToSend.isEmpty()) return@launch

			val reportEventsResult = apiService.reportEvents(eventsToSend)

			reportEventsResult.postInterval?.let { eventsServiceTimer.postInterval = it }

			when (reportEventsResult.status)
			{
				ReportEventStatus.SUCCESS ->
				{
					Logger.debug("EventsService: Events send success. Remove ${eventsToSend.size} events from queue.")
					eventsQueue.removeAll(eventsToSend)

					if (eventsQueue.hasEventsToSend())
					{
						Logger.debug("EventsService: Events queue have more events to send")
						flush()
					} else Logger.debug("EventsService: Queue is empty")
				}
				ReportEventStatus.ERROR_NETWORK,
				ReportEventStatus.ERROR_BAD_RESPONSE ->
				{
					Logger.warn("EventsService: Events not send! Network error! Try next time")
				}
				ReportEventStatus.ERROR_BAD_REQUEST ->
				{
					if (apiService.hasIdentify())
					{
						eventsQueue.removeAll(eventsToSend)
						Logger.error("EventsService: Events not send! Wrong events! Remove from queue")
					} else Logger.error("EventsService: Events not send! Wrong events! Wait for identify")
				}
			}
		}
	}

    override fun readyToFlush()
    {
        flush()
    }
}
