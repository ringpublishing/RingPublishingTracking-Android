package com.ringpublishing.tracking.internal.delegate

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.decorator.EventDecorator
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.EventsService

internal class EventsReporter(private val eventsService: EventsService, private val eventDecorator: EventDecorator)
{

	fun reportEvent(event: Event)
	{
		Logger.debug("App reported event $event")
		val decoratedEvent = eventDecorator.decorate(event)
		eventsService.addEvent(decoratedEvent)
	}

	fun reportEvents(events: List<Event>)
	{
		Logger.debug("App reported events $events")
		eventsService.addEvents(events.map { eventDecorator.decorate(it) })
	}
}
