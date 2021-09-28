package com.ringpublishing.tracking.internal.service.queue

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.log.Logger

internal class EventsQueue(private val eventSizeCalculator: EventSizeCalculator)
{

    private val queue = mutableListOf<Event>()
    private val eventsToSend = mutableListOf<Event>()
    private val toBigEvents = mutableListOf<Event>()

    fun add(event: Event)
    {
        queue.add(event)
    }

    fun addAll(event: List<Event>)
    {
        queue.addAll(event)
    }

    fun getMaximumEventsToSend(): List<Event>
    {
        eventsToSend.clear()
        eventSizeCalculator.calculateBodyElementsSize()

        var eventsToSendSize = 0L

        queue.forEach {
            val eventSize = eventSizeCalculator.getSizeInBytes(it)

            if (eventSize < Constants.maxEventSize)
                {
                    if (eventSizeCalculator.isLowerThanMaxRequestSize(eventsToSendSize, eventSize))
                        {
                            eventsToSend.add(it)
                            eventsToSendSize += eventSize
                            Logger.debug(
                                "TrackingQueue: Added event with size: $eventSize. " +
                                    "Events to send size: $eventsToSendSize. " +
                                    "Available: ${eventSizeCalculator.available(eventsToSendSize)} $it",
                            )
                        } else {
                        return@forEach
                    }
                } else {
                toBigEvents.add(it)
                Logger.warn(
                    "TrackingQueue: Event is to big. Maximum size is ${Constants.maxEventSize}. " +
                        "Event size is $eventSize $it"
                )
            }
        }

        queue.removeAll(toBigEvents)
        toBigEvents.clear()

        return eventsToSend
    }

    fun hasEventsToSend() = queue.isNotEmpty()

    fun removeAll(eventsToSend: List<Event>)
    {
        queue.removeAll(eventsToSend)
    }
}
