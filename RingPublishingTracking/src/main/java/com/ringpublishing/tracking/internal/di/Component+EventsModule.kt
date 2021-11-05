package com.ringpublishing.tracking.internal.di

import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.decorator.EventDecorator
import com.ringpublishing.tracking.internal.service.EventsService
import com.ringpublishing.tracking.internal.service.queue.EventSizeCalculator
import com.ringpublishing.tracking.internal.service.queue.EventsQueue
import com.ringpublishing.tracking.internal.service.queue.TooBigEventReplacement
import com.ringpublishing.tracking.internal.service.timer.EventsServiceTimer

private val tooBigEventReplacement: TooBigEventReplacement by lazy { TooBigEventReplacement(Component.provideConfigurationManager()) }

internal fun Component.provideTooBigEventReplacement() = tooBigEventReplacement

private val eventsQueue: EventsQueue by lazy { EventsQueue(Component.provideEventSizeCalculator(), Component.provideTooBigEventReplacement()) }

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

internal fun Component.provideEventsService(configurationManager: ConfigurationManager): EventsService
{
	if (eventsService == null)
	{
		eventsService = EventsService(
			Component.provideApiService(configurationManager.ringPublishingTrackingConfiguration),
			Component.provideEventsQueue(),
			Component.provideEventsServiceTimer(),
			configurationManager
		)
	}

	return eventsService!!
}

private var eventDecorator: EventDecorator? = null

internal fun Component.provideEventDecorator(configurationManager: ConfigurationManager): EventDecorator
{
	if (eventDecorator == null)
	{
		eventDecorator = EventDecorator(
			configurationManager,
			provideApplication(),
			Component.provideGson(),
			Component.provideWindowSizeInfo(),
			Component.provideScreenSizeInfo()
		)
	}

	return eventDecorator!!
}
