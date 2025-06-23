/*
 *  Created by Grzegorz Małopolski on 10/13/21, 5:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.KeepAliveContentStatus
import com.ringpublishing.tracking.internal.EventsReporter
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewComponentSource
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewMetadata
import com.ringpublishing.tracking.internal.effectivepageview.EffectivePageViewTriggerSource
import com.ringpublishing.tracking.internal.factory.EffectivePageViewEventFactory
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.timer.KeepAliveSendTimerCallback
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

internal class KeepAliveReporter(
    private val eventsReporter: EventsReporter,
    screenSizeInfo: ScreenSizeInfo,
    private val lifecycleOwner: LifecycleOwner,
    gson: Gson,
    private val effectivePageViewEventFactory: EffectivePageViewEventFactory
) : KeepAliveSendTimerCallback, DefaultLifecycleObserver
{

	private val timer = KeepAliveTimer(this)

	private val eventBuilder = KeepAliveEventBuilder(screenSizeInfo, gson)

	private var contentMetadata: ContentMetadata? = null

	private var isPaused = false

	private var isWorking = false

	private var isInBackground = false

	private var collectedData = CopyOnWriteArrayList<KeepAliveMetadata>()

	private var dataSourceDelegate: WeakReference<KeepAliveDataSource>? = null

    var lastContentStatus: KeepAliveContentStatus? = null

    fun start(contentMetadata: ContentMetadata, contentKeepAliveDataSource: KeepAliveDataSource, partiallyReloaded: Boolean) {
        Logger.debug("KeepAliveReporter: start()")

        if (partiallyReloaded && contentMetadata == this.contentMetadata) {
            dataSourceDelegate = WeakReference(contentKeepAliveDataSource)
            resume()
            return
        }

        stop()

        this.contentMetadata = contentMetadata
        dataSourceDelegate = WeakReference(contentKeepAliveDataSource)

        timer.start()
        isWorking = true
        isPaused = false
        isInBackground = false
        lifecycleOwner.lifecycle.addObserver(this)
        eventsReporter.resetEPVEventSentState()
    }

	fun pause()
	{
		Logger.debug("KeepAliveReporter: pause()")
		if (contentMetadata == null || isPaused)
		{
			Logger.warn("KeepAliveReporter: pause() ignored")
			return
		}
		isPaused = true
		isWorking = false
		timer.pause()
	}

	fun resume()
	{
		Logger.debug("KeepAliveReporter: resume()")
		if (contentMetadata == null || !isPaused)
		{
			Logger.warn("KeepAliveReporter: resume() ignored")
			return
		}
		timer.resume()
		isPaused = false
		isWorking = true
		timer.scheduleActivityTimer()
		timer.scheduleSendTimer()
	}

	fun stop()
	{
		Logger.debug("KeepAliveReporter: stop()")
		if (!collectedData.isEmpty())
		{
			sendMeasurements()
		}

        collectedData.clear()
        isPaused = false
        isWorking = false
        isInBackground = false
        timer.stop()
        contentMetadata = null
        lastContentStatus = null
        dataSourceDelegate = null
        lifecycleOwner.lifecycle.removeObserver(this)
	}

	@Synchronized
	override fun onSendTimer()
	{
		Logger.debug("KeepAliveReporter: onSendTimer() time. Start take and send measurement isWorking $isWorking")
		if (isWorking)
		{
			takeMeasurements(KeepAliveMeasureType.SEND_TIMER)
			sendMeasurements()
			timer.scheduleSendTimer()
		}
	}

	@Synchronized
	override fun onActivityTimer()
	{
		Logger.debug("KeepAliveReporter: onActivityTimer() time. Start take measurement isWorking $isWorking")
		if (isWorking)
		{
			takeMeasurements(KeepAliveMeasureType.ACTIVITY_TIMER)
		}
	}

    @Synchronized
    override fun onEffectivePageViewTimer() {
        Logger.debug("KeepAliveReporter: onEffectivePageViewTimer() time. Start take measurement isWorking $isWorking")
        if (isWorking) {
            takeMeasurements(KeepAliveMeasureType.EFFECTIVE_PAGE_VIEW)
        }
    }

	@Synchronized
	private fun sendMeasurements()
	{
		if (collectedData.isEmpty())
		{
			Logger.warn("KeepAliveReporter: Nothing to send. There is no any measurements taken yet.")
			return
		}

		val event = eventBuilder.create(contentMetadata, collectedData)

		Logger.info("KeepAliveReporter: Sending measurements event $event")

		eventsReporter.reportEvent(event)
		collectedData.clear()
	}

    @Synchronized
    private fun takeMeasurements(measureType: KeepAliveMeasureType) {
        getKeepAliveMetadata(measureType)?.let { metadata ->
            handleMeasurement(metadata)
        } ?: run {
            Logger.warn("KeepAliveReporter: Failed to take measurement for type $measureType")
        }
    }

    @Synchronized
    private fun getKeepAliveMetadata(measureType: KeepAliveMeasureType): KeepAliveMetadata? {
        val timeFromStart = timer.timeFromStartInForeground()
        val dataSourceDelegate = dataSourceDelegate?.get()
        val currentContentMetadata = contentMetadata

        if (timeFromStart == null || dataSourceDelegate == null || currentContentMetadata == null) {
            Logger.warn("KeepAliveReporter: Wrong measurement $timeFromStart $dataSourceDelegate $currentContentMetadata")
            return null
        }

        val contentStatus = dataSourceDelegate.didAskForKeepAliveContentStatus(currentContentMetadata)

        return contentStatus?.let { status ->
            KeepAliveMetadata(status, timeFromStart, true, measureType)
        } ?: run {
            Logger.warn("KeepAliveReporter: Wrong data for take measurement. ContentStatus is null")
            null
        }
    }

    private fun handleMeasurement(data: KeepAliveMetadata) {
        when (data.measureType) {
            KeepAliveMeasureType.EFFECTIVE_PAGE_VIEW -> handleEffectivePageViewMeasurement(data)
            else -> handlePageViewMeasurement(data)
        }
    }

    private fun handleEffectivePageViewMeasurement(data: KeepAliveMetadata) {
        lastContentStatus = data.contentStatus

        val effectivePageViewMetadata = EffectivePageViewMetadata(
            componentSource = EffectivePageViewComponentSource.Scroll,
            triggerSource = EffectivePageViewTriggerSource.Scroll,
            measurement = data.contentStatus
        )

        if(!eventsReporter.shouldReportEPVEvent(effectivePageViewMetadata)) {
            return
        }

        val event = effectivePageViewEventFactory.create(
            contentMetadata = contentMetadata,
            effectivePageViewMetadata = effectivePageViewMetadata
        )

        Logger.info("KeepAliveReporter: Sending effectivePageView event $event")
        eventsReporter.reportEvent(event)
    }

    private fun handlePageViewMeasurement(data: KeepAliveMetadata) {
        Logger.debug("KeepAliveReporter: Add data: $data")
        collectedData.add(data)
    }

    override fun onStart(owner: LifecycleOwner)
    {
        if (contentMetadata == null || isPaused || !isInBackground)
        {
            Logger.debug("KeepAliveReporter: onEnterForeground() ignored")
            return
        }

        isInBackground = false

        Logger.debug("KeepAliveReporter: onEnterForeground()")

        timer.resumeBackground()

        takeMeasurements(KeepAliveMeasureType.DOCUMENT_ALIVE)

        timer.scheduleActivityTimer()
        timer.scheduleSendTimer()
    }

    override fun onStop(owner: LifecycleOwner)
    {
        if (contentMetadata == null || isPaused)
        {
            Logger.warn("KeepAliveReporter: onEnterBackground() ignored")
            return
        }
        Logger.debug("KeepAliveReporter: onEnterBackground()")

        timer.pauseBackground()
        takeMeasurements(KeepAliveMeasureType.DOCUMENT_INACTIVE)
        isInBackground = true
    }
}
