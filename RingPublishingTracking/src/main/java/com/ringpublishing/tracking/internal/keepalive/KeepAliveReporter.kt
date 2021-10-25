/*
 *  Created by Grzegorz Małopolski on 10/13/21, 5:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.internal.EventsReporter
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.timer.KeepAliveSendTimerCallback
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

internal class KeepAliveReporter(private val eventsReporter: EventsReporter, screenSizeInfo: ScreenSizeInfo) : KeepAliveSendTimerCallback, LifecycleObserver
{

	private val timer = KeepAliveTimer(this)

	private val eventBuilder = KeepAliveEventBuilder(screenSizeInfo)

	private var contentMetadata: ContentMetadata? = null

	private var isPaused = false

	private var isInBackground = false

	private var collectedData = CopyOnWriteArrayList<KeepAliveMetadata>()

	private var dataSourceDelegate: WeakReference<KeepAliveDataSource>? = null

	fun start(contentMetadata: ContentMetadata, contentKeepAliveDataSource: KeepAliveDataSource, partiallyReloaded: Boolean)
	{
		Logger.debug("KeepAliveReporter: start()")

		if (partiallyReloaded && contentMetadata == this.contentMetadata)
		{
			dataSourceDelegate = WeakReference(contentKeepAliveDataSource)
			resume()
			return
		}

		stop()

		this.contentMetadata = contentMetadata
		dataSourceDelegate = WeakReference(contentKeepAliveDataSource)

		timer.start()
		isPaused = false
		isInBackground = false

		ProcessLifecycleOwner.get().lifecycle.addObserver(this)
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
		isInBackground = false
		timer.stop()
		contentMetadata = null
		dataSourceDelegate = null
		ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
	}

	@Synchronized
	override fun onSendTimer()
	{
		Logger.debug("KeepAliveReporter: onSendTimer() time. Start take and send measurement")
		takeMeasurements(KeepAliveMeasureType.SEND_TIMER)
		sendMeasurements()
		timer.scheduleSendTimer()
	}

	override fun onActivityTimer()
	{
		Logger.debug("KeepAliveReporter: onActivityTimer() time. Start take measurement")
		takeMeasurements(KeepAliveMeasureType.ACTIVITY_TIMER)
		timer.scheduleActivityTimer()
	}

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

	private fun takeMeasurements(measureType: KeepAliveMeasureType)
	{
		val timeFromStart = timer.timeFromStartInForeground()
		val dataSourceDelegate = dataSourceDelegate?.get()
		val currentContentMetadata = contentMetadata

		if (timeFromStart == null || dataSourceDelegate == null || currentContentMetadata == null)
		{
			Logger.warn("KeepAliveReporter: Wrong measurement $timeFromStart $dataSourceDelegate $currentContentMetadata")
			stop()
			return
		}

		val contentStatus = dataSourceDelegate.didAskForKeepAliveContentStatus(currentContentMetadata)

		contentStatus?.let { status ->
			val data = KeepAliveMetadata(status, timeFromStart, true, measureType)
			Logger.debug("KeepAliveReporter: Add data: $data")
			collectedData.add(data)
		} ?: run {
			Logger.warn("KeepAliveReporter: Wrong data for take measurement. ContentStatus is null")
		}
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	fun onEnterForeground()
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

	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	fun onEnterBackground()
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
