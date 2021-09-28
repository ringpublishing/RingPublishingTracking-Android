package com.ringpublishing.tracking.internal.delegate

import com.ringpublishing.tracking.delegate.RingPublishingTrackingKeepAliveDataSource
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.delegate.RingPublishingTrackingDelegate
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.EventsService

internal class ReportDelegate(private val eventsService: EventsService, private val ringPublishingTrackingDelegate: RingPublishingTrackingDelegate?)
{

    fun reportEvent(event: Event)
    {
        Logger.debug("App reported event $event")
        eventsService.addEvent(event)
    }

    fun reportEvents(events: List<Event>)
    {
        Logger.debug("App reported events $events")
        eventsService.addEvents(events)
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

    fun reportPageView(partialReload: Boolean)
    {
        Logger.debug("App report PageView event partialReload=$partialReload")
    }

    fun reportContentPageView(contentMetadata: ContentMetadata, partialReload: ContentPageViewSource, delegate: Boolean, dataSource: RingPublishingTrackingKeepAliveDataSource)
    {
        Logger.debug("App report ContentPageView event contentMetadata=$contentMetadata partialReload=$partialReload delegate=$delegate contentKeepAliveDataSource$dataSource")
    }
}
