package com.ringpublishing.tracking.internal

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewMetadata
import com.ringpublishing.tracking.internal.decorator.EventDecorator
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewTriggerSource
import com.ringpublishing.tracking.internal.factory.EventType
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.EventsService
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo

internal class EventsReporter(
    private val eventsService: EventsService,
    private val eventDecorator: EventDecorator,
    private val screenSizeInfo: ScreenSizeInfo,
    private val configuration: ConfigurationManager
) {

    private var isEPVEventSent = false

    fun reportEvent(event: Event) {
        val decoratedEvent = eventDecorator.decorate(event)
        Logger.debug("App reported event $event")
        eventsService.addEvent(decoratedEvent)
        handleEPVEventReport(event)
    }

    fun reportEvents(events: List<Event>) {
        Logger.debug("App reported events $events")
        eventsService.addEvents(events.map { eventDecorator.decorate(it) })
    }

    fun shouldReportEPVEvent(metadata: EffectivePageViewMetadata): Boolean {
        val viewportHeight = screenSizeInfo.getScreenSizePxFromMetrics().height
        val offset = metadata.measurement.scrollOffsetPx
        val triggerSource = metadata.triggerSource

        return configuration.shouldReportEffectivePageViewEvent()
                && !isEPVEventSent
                && (triggerSource != EffectivePageViewTriggerSource.Scroll || (offset >= 2 * viewportHeight))
    }

    fun resetEPVEventSentState() {
        isEPVEventSent = false
    }

    private fun handleEPVEventReport(event: Event) {
        if (event.name == EventType.POLARIS.text) {
            isEPVEventSent = true
        }
    }
}
