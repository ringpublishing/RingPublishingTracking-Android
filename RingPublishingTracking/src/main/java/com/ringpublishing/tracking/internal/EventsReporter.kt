package com.ringpublishing.tracking.internal

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.decorator.EventDecorator
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.EventsService

internal class EventsReporter(private val eventsService: EventsService, private val eventDecorator: EventDecorator)
{

	fun reportEvent(event: Event)
	{
		val decoratedEvent = eventDecorator.decorate(event)
		Logger.debug("App reported event $event")
		eventsService.addEvent(decoratedEvent)
	}

	fun reportEvents(events: List<Event>)
	{
		Logger.debug("App reported events $events")
		eventsService.addEvents(events.map { eventDecorator.decorate(it) })
	}
}
