package com.ringpublishing.tracking.internal.di

import com.ringpublishing.tracking.internal.decorator.EventDecorator
import com.ringpublishing.tracking.internal.delegate.ConfigurationDelegate
import com.ringpublishing.tracking.internal.service.EventsService
import com.ringpublishing.tracking.internal.service.queue.EventSizeCalculator
import com.ringpublishing.tracking.internal.service.queue.EventsQueue
import com.ringpublishing.tracking.internal.service.timer.EventsServiceTimer

private val eventsQueue: EventsQueue by lazy { EventsQueue(Component.provideEventSizeCalculator()) }

internal fun Component.provideEventsQueue() = eventsQueue

private val eventSizeCalculator: EventSizeCalculator by lazy {
	EventSizeCalculator(
		Component.provideGson(),
		Component.provideApiRepository(),
		Component.provideUserRepository()
	)
}

private fun Component.provideEventSizeCalculator() = eventSizeCalculator

private val eventsServiceTimer: EventsServiceTimer by lazy { EventsServiceTimer() }

internal fun Component.provideEventsServiceTimer() = eventsServiceTimer

private var eventsService: EventsService? = null

internal fun Component.provideEventsService(configurationDelegate: ConfigurationDelegate): EventsService
{
	if (eventsService == null)
	{
		eventsService = EventsService(
			Component.provideApiService(configurationDelegate.ringPublishingTrackingConfiguration),
			Component.provideEventsQueue(),
			Component.provideEventsServiceTimer(),
			configurationDelegate
		)
	}

	return eventsService!!
}

private var eventDecorator: EventDecorator? = null

internal fun Component.provideEventDecorator(configurationDelegate: ConfigurationDelegate): EventDecorator
{
	if (eventDecorator == null)
	{
		eventDecorator = EventDecorator(configurationDelegate, provideContext(), Component.provideGson())
	}

	return eventDecorator!!
}
