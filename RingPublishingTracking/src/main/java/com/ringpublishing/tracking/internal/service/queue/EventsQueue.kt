package com.ringpublishing.tracking.internal.service.queue

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.log.Logger
import java.util.concurrent.CopyOnWriteArrayList

internal class EventsQueue(private val eventSizeCalculator: EventSizeCalculator, private val tooBigEventReplacement: TooBigEventReplacement)
{

    private val queue = CopyOnWriteArrayList<Event>()
    private val eventsToSend = CopyOnWriteArrayList<Event>()

	fun add(event: Event)
	{
		val eventSize = eventSizeCalculator.getSizeInBytes(event)

		val eventWithSize = if (eventSize >= Constants.maxEventSize)
		{
			tooBigEventReplacement.replace(event, eventSize)
		} else event

		queue.add(eventWithSize)
	}

    fun addAll(events: List<Event>) = events.forEach { add(it) }

	@Synchronized
	fun getMaximumEventsToSend(): List<Event>
	{
		eventsToSend.clear()
		eventSizeCalculator.calculateBodyElementsSize()

		var eventsToSendSize = 0L

		queue.forEach {
			val eventSize = eventSizeCalculator.getSizeInBytes(it)

			if (eventSizeCalculator.isBiggerThanMaxRequestSize(eventsToSendSize, eventSize))
			{
				return@forEach
			}

			eventsToSend.add(it)
			eventsToSendSize += eventSize

			Logger.debug(
				"TrackingQueue: Added event with size: $eventSize. " +
						"Events to send size: $eventsToSendSize. " +
						"Available: ${eventSizeCalculator.available(eventsToSendSize)} $it",
			)
		}

		return eventsToSend
	}

    fun hasEventsToSend() = queue.isNotEmpty()

    fun removeAll(eventsToSend: List<Event>)
    {
        queue.removeAll(eventsToSend)
    }
}
