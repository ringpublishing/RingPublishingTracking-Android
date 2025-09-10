/*
 *  Created by Grzegorz Małopolski on 9/27/21, 4:50 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.KeepAliveContentStatus
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.data.TrackingIdentifier
import com.ringpublishing.tracking.delegate.RingPublishingTrackingDelegate
import com.ringpublishing.tracking.delegate.RingPublishingTrackingKeepAliveDataSource
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.EventsReporter
import com.ringpublishing.tracking.internal.di.Component
import com.ringpublishing.tracking.internal.di.provideApiRepository
import com.ringpublishing.tracking.internal.di.provideEventDecorator
import com.ringpublishing.tracking.internal.di.provideEventsService
import com.ringpublishing.tracking.internal.di.provideGson
import com.ringpublishing.tracking.internal.di.provideScreenSizeInfo
import com.ringpublishing.tracking.internal.di.provideSnakeCaseGson
import com.ringpublishing.tracking.internal.factory.AudioEventsFactory
import com.ringpublishing.tracking.internal.factory.AureusEventFactory
import com.ringpublishing.tracking.internal.factory.EffectivePageViewEventFactory
import com.ringpublishing.tracking.internal.factory.EventsFactory
import com.ringpublishing.tracking.internal.factory.PaidEventsFactory
import com.ringpublishing.tracking.internal.factory.VideoEventsFactory
import com.ringpublishing.tracking.internal.keepalive.KeepAliveDataSource
import com.ringpublishing.tracking.internal.keepalive.KeepAliveReporter
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.listener.LogListener
import java.lang.ref.WeakReference

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
object RingPublishingTracking : KeepAliveDataSource {

    /*
    * @property trackingIdentifier Tracking identifier assigned by the module for this device
    */
    var trackingIdentifier: TrackingIdentifier? = null
        set(value) {
            field = value
            value?.let { delegate?.get()?.ringPublishingTrackingDidAssignTrackingIdentifier(this, it) }
        }
        get() {
            if (!Component.initialized) return null
            return Component.provideApiRepository().readTrackingIdentifier()
        }

    internal var trackingIdentifierError: TrackingIdentifierError? = null
        set(value) {
            field = value
            value?.let { delegate?.get()?.ringPublishingTrackingDidFailToRetrieveTrackingIdentifier(this, value) }
        }

    /**
     * Property indicating if tracking module is initialized.
     */
    private var isInitialized = false

    /**
     * Initialize all needed parameters needed to report events.
     * Should be called once in main point of application.
     *
     * @param application is Android Application
     * @param ringPublishingTrackingConfiguration is tenant for this application
     * @param ringPublishingTrackingDelegate RingPublishingTrackingDelegate which will be set as WeakReference
     */
    fun initialize(
        application: Application,
        ringPublishingTrackingConfiguration: RingPublishingTrackingConfiguration,
        ringPublishingTrackingDelegate: RingPublishingTrackingDelegate?,
    ) {
        configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)
        Component.initComponent(application, configurationManager)
        eventsReporter = EventsReporter(
            eventsService = Component.provideEventsService(configurationManager),
            eventDecorator = Component.provideEventDecorator(configurationManager),
            screenSizeInfo = Component.provideScreenSizeInfo(),
            configuration = configurationManager
        )
        effectivePageViewEventFactory = EffectivePageViewEventFactory(
            screenSizeInfo = Component.provideScreenSizeInfo(),
            gson = Component.provideGson(),
        )
        keepAliveReporter = KeepAliveReporter(
            eventsReporter = eventsReporter,
            screenSizeInfo = Component.provideScreenSizeInfo(),
            lifecycleOwner = ProcessLifecycleOwner.get(),
            gson = Component.provideGson(),
            effectivePageViewEventFactory = effectivePageViewEventFactory
        )
        delegate = WeakReference(ringPublishingTrackingDelegate)
        isInitialized = true
        Logger.debug("RingPublishingTracking initialized successfully")
    }

    /**
     * Turn on or off debug mode
     * By default debug mode is disabled
     * Debug mode enable debug logs
     *
     * @param enabled turn on debug logs when is set to true
     */
    fun setDebugMode(enabled: Boolean) {
        configurationManager.setDebugMode(enabled)
    }

    /**
     * Turn on or off send events to server
     * By default 'enabled' is false, so events are send
     *
     * @param enabled can be set to true. Then events are not send
     */
    fun setOptOutMode(enabled: Boolean) {
        configurationManager.setOptOutMode(enabled)
    }

    /**
     * Add custom logger
     *
     * @param logListener is custom application logger that implement 'LogListener'
     */
    fun addLogListener(logListener: LogListener) {
        Logger.addLogListener(logListener)
    }

    /**
     * Remove already added custom logger
     *
     * @param logListener is logger to remove
     */
    fun removeLogListener(logListener: LogListener) {
        Logger.removeLogListener(logListener)
    }

    /**
     * Generic event
     * Reports generic event which is not predefined in module
     *
     * @param event: Event
     */
    fun reportEvent(event: Event) = ifInitializedOrWarn {
        eventsReporter.reportEvent(event)
    }

    /**
     * Generic event
     * Reports many generic events at once which are not predefined in module
     *
     * @param events: List of events
     */
    fun reportEvents(events: List<Event>) = ifInitializedOrWarn {
        eventsReporter.reportEvents(events)
    }

    /**
     * Checks if RingPublishingTracking is initialized and executes the block if it is or warns user otherwise
     */
    @Suppress("UNUSED_EXPRESSION")
    internal inline fun <T> ifInitializedOrWarn(block: RingPublishingTracking.() -> T) {
        if (!isInitialized) {
            Logger.warn("RingPublishingTracking is not initialized.")
        } else {
            block()
        }
    }

    override fun didAskForKeepAliveContentStatus(contentMetadata: ContentMetadata): KeepAliveContentStatus? {
        return keepAliveDelegate?.get()?.ringPublishingTrackingDidAskForKeepAliveContentStatus(this, contentMetadata)
    }

    internal val configurationManager = ConfigurationManager()
    internal lateinit var eventsReporter: EventsReporter
    internal lateinit var keepAliveReporter: KeepAliveReporter
    internal lateinit var effectivePageViewEventFactory: EffectivePageViewEventFactory
    internal val eventsFactory = EventsFactory(Component.provideGson())
    internal val videoEventsFactory = VideoEventsFactory(Component.provideGson())
    internal val audioEventsFactory = AudioEventsFactory(Component.provideGson())
    internal val paidEventsFactory = PaidEventsFactory(Component.provideGson())
    internal val aureusEventFactory = AureusEventFactory(Component.provideSnakeCaseGson(), eventsFactory)
    var delegate: WeakReference<RingPublishingTrackingDelegate>? = null
    internal var keepAliveDelegate: WeakReference<RingPublishingTrackingKeepAliveDataSource>? = null
}
