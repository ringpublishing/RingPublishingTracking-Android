package com.ringpublishing.tracking.internal.delegate

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.delegate.RingPublishingTrackingDelegate
import com.ringpublishing.tracking.delegate.RingPublishingTrackingKeepAliveDataSource
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.data.EventName
import com.ringpublishing.tracking.internal.decorator.EventDecorator
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.EventsService

internal class ReportDelegate(
    private val eventsService: EventsService,
    private val ringPublishingTrackingDelegate: RingPublishingTrackingDelegate?,
    private val eventDecorator: EventDecorator,
    private val configurationDelegate: ConfigurationDelegate,
)
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

	fun reportClick(elementName: String?)
	{
		Logger.debug("App report Click event elementName=$elementName")
	}

	fun reportUserAction(actionName: String, actionSubtypeName: String, parameters: Map<String, Any>)
	{
		Logger.debug("App report UserAction event actionName=$actionName actionSubtypeName=$actionSubtypeName parameters=$parameters")
	}

	fun reportUserAction(actionName: String, actionSubtypeName: String, parameters: String)
	{
		Logger.debug("App report UserAction event actionName=$actionName actionSubtypeName=$actionSubtypeName parameters=$parameters")
	}

	fun reportPageView(currentStructurePath: List<String>, partiallyReloaded: Boolean)
	{
		Logger.debug("App report PageView event currentStructurePath=$currentStructurePath partialReload=$partiallyReloaded")

		configurationDelegate.updateCurrentStructurePath(currentStructurePath)
		configurationDelegate.currentIsPartialView = partiallyReloaded

		if (partiallyReloaded)
		{
			configurationDelegate.newSecondaryId()
		}

		// todo: should be eventDefaultAnalyticsSystemName ?
		val event = Event(Constants.eventDefaultAnalyticsSystemName, EventName.PAGE_VIEW.text)

		// todo: append parameters to this event

		reportEvent(event)
	}

	fun reportContentPageView(
	    contentMetadata: ContentMetadata,
	    contentPageViewSource: ContentPageViewSource,
	    currentStructurePath: List<String>,
	    partiallyReloaded: Boolean,
	    contentKeepAliveDataSource: RingPublishingTrackingKeepAliveDataSource,
	)
	{
		Logger.debug("App report ContentPageView event " +
				"contentMetadata=$contentMetadata " +
				"contentPageViewSource=$contentPageViewSource " +
				"currentStructurePath=$currentStructurePath" +
				"partialReload=$partiallyReloaded ")

		configurationDelegate.updateCurrentPublicationUrl(contentMetadata.publicationUrl)
		configurationDelegate.updateCurrentStructurePath(currentStructurePath)
		configurationDelegate.currentIsPartialView = partiallyReloaded

		configurationDelegate.newPrimaryId()

		if (partiallyReloaded)
		{
			configurationDelegate.newSecondaryId()
		}

		val event = Event(contentMetadata.sourceSystemName, EventName.CONTENT_PAGE_VIEW.text)
		// todo: append parameters to this event

		reportEvent(event)
	}
}
