/*
 *  Created by Grzegorz Małopolski on 9/27/21, 4:50 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

import android.app.Application
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.delegate.RingPublishingTrackingDelegate
import com.ringpublishing.tracking.internal.delegate.ConfigurationManager
import com.ringpublishing.tracking.internal.delegate.EventsReporter
import com.ringpublishing.tracking.internal.di.Component
import com.ringpublishing.tracking.internal.di.provideEventDecorator
import com.ringpublishing.tracking.internal.di.provideEventsService
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.listener.LogListener

/**
 * Main 'RingPublishingTracking' module class to interact with application
 * Used to report application events like page view, user click or start and stop application lifecycle observation.
 *
 * Must be initialized once by method 'initialize(...)' before report any event in main point of application
 *
 * Module can report specific events using methods:
 * - reportClick
 * - reportUserAction
 * - reportPageView
 * - reportContentPageView
 *
 * Other events can be reported by method 'report'
 *
 * 'RingPublishingTracking' can also configure in module:
 *
 * - Enable debug logs by method 'setDebugMode(enabled: Boolean)' - by default disabled
 * - Disable send events to server by method 'setOptOutMode(enabled: Boolean)' - by default disabled
 * - Add custom application logger by method 'addLogListener(logListener: LogListener)'
 * - Remove custom application logger 'removeLogListener(logListener: LogListener)'
 */
@Suppress("unused")
object RingPublishingTracking
{

	/*
	* @property trackingIdentifier Tracking identifier assigned by the module for this device
	*/
	var trackingIdentifier: String? = null

	/**
	 * Initialize all needed parameters needed to report events.
	 * Should be called once in main point of application.
	 *
	 * @param application is Android Application
	 * @param ringPublishingTrackingConfiguration is tenant for this application
	 */
	fun initialize(application: Application, ringPublishingTrackingConfiguration: RingPublishingTrackingConfiguration, ringPublishingTrackingDelegate: RingPublishingTrackingDelegate?)
	{
		Component.initComponent(application)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)
		eventsReporter = EventsReporter(Component.provideEventsService(configurationManager), Component.provideEventDecorator(configurationManager))
	}

	/**
	 * Tur on or off debug mode
	 * By default debug mode is disabled
	 * Debug mode enable debug logs
	 *
	 * @param enabled turn on debug logs when is set to true
	 */
	fun setDebugMode(enabled: Boolean)
	{
		configurationManager.setDebugMode(enabled)
	}

	/**
	 * Turn on or off send events to server
	 * By default 'enabled' is false, so events are send
	 *
	 * @param enabled can be set to true. Then events are not send
	 */
	fun setOptOutMode(enabled: Boolean)
	{
		configurationManager.setOptOutMode(enabled)
	}

	/**
	 * Add custom logger
	 *
	 * @param logListener is custom application logger that implement 'LogListener'
	 */
	fun addLogListener(logListener: LogListener)
	{
		Logger.addLogListener(logListener)
	}

	/**
	 * Remove already added custom logger
	 *
	 * @param logListener is logger to remove
	 */
	fun removeLogListener(logListener: LogListener)
	{
		Logger.removeLogListener(logListener)
	}

	/**
	 * Generic event
	 * Reports generic event which is not predefined in module
	 *
	 * @param event: Event
	 */
	fun reportEvent(event: Event)
	{
		eventsReporter.reportEvent(event)
	}

	/**
	 * Generic event
	 * Reports many generic events at once which are not predefined in module
	 *
	 * @param events: List of events
	 */
	fun reportEvents(events: List<Event>)
	{
		eventsReporter.reportEvents(events)
	}

	internal val configurationManager = ConfigurationManager()
	internal lateinit var eventsReporter: EventsReporter
}
