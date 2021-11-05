/*
 *  Created by Grzegorz Małopolski on 10/28/21, 3:46 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.service

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.ApiService
import com.ringpublishing.tracking.internal.service.EventsService
import com.ringpublishing.tracking.internal.service.queue.EventsQueue
import com.ringpublishing.tracking.internal.service.result.ReportEventResult
import com.ringpublishing.tracking.internal.service.result.ReportEventStatus
import com.ringpublishing.tracking.internal.service.timer.EventsServiceTimer
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class EventsServiceTest
{

	@MockK
	internal lateinit var apiService: ApiService

	@MockK
	internal lateinit var eventsQueue: EventsQueue

	@MockK
	internal lateinit var eventsServiceTimer: EventsServiceTimer

	@MockK
	internal lateinit var configurationManager: ConfigurationManager

	@MockK
	lateinit var event: Event

	@MockK
	lateinit var event2: Event

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun addEvent_WhenDefault_ThenCanAddEvent()
	{
		every { configurationManager.isSendEventsBlocked() } returns false
		val eventService = EventsService(apiService, eventsQueue, eventsServiceTimer, configurationManager)

		eventService.addEvent(event)

		verify { eventsQueue.add(event) }
		verify { eventsServiceTimer.scheduleFlush()}
	}

	@Test
	fun addEvent_WhenOptOutEnabled_ThenEventNotAdded()
	{
		every { configurationManager.isSendEventsBlocked() } returns true
		val eventService = EventsService(apiService, eventsQueue, eventsServiceTimer, configurationManager)

		eventService.addEvent(event)

		verify(exactly = 0) { eventsQueue.add(event) }
		verify(exactly = 0) { eventsServiceTimer.scheduleFlush()}
	}

	@Test
	fun addEvents_WhenDefault_ThenCanAddEvents()
	{
		every { configurationManager.isSendEventsBlocked() } returns false

		val eventService = EventsService(apiService, eventsQueue, eventsServiceTimer, configurationManager)
		val list = mutableListOf<Event>()
		list.add(event)
		list.add(event2)

		eventService.addEvents(list)

		verify { eventsQueue.addAll(list) }
		verify { eventsServiceTimer.scheduleFlush()}
	}

	@Test
	fun addEvents_WhenOptOutEnabled_ThenEventsNotAdded()
	{
		every { configurationManager.isSendEventsBlocked() } returns true
		val eventService = EventsService(apiService, eventsQueue, eventsServiceTimer, configurationManager)
		val list = mutableListOf<Event>()
		list.add(event)
		list.add(event2)

		eventService.addEvents(list)

		verify(exactly = 0) { eventsQueue.addAll(list) }
		verify(exactly = 0) { eventsServiceTimer.scheduleFlush()}
	}

	@Test
	fun readyToFlush_WhenFLushOneEvent_ThenApiServiceReportOneEvent()
	{
		coEvery { configurationManager.isSendEventsBlocked() } returns false
		val list = mutableListOf<Event>()
		list.add(event)
		coEvery { eventsQueue.getMaximumEventsToSend() } returns list
		coEvery { eventsQueue.hasEventsToSend() } returns false
		coEvery { apiService.reportEvents(any()) } returns ReportEventResult(ReportEventStatus.SUCCESS, 500)
		val eventService = EventsService(apiService, eventsQueue, eventsServiceTimer, configurationManager)
		eventService.addEvent(event)

		eventService.readyToFlush()

		coVerify(exactly = 1, timeout = 10000) { apiService.reportEvents(any()) }
	}

	@Test
	fun readyToFlush_WhenFLushMOreEvents_ThenApiServiceReportEvents()
	{
		val list = mutableListOf<Event>()
		list.add(event)
		list.add(event2)
		coEvery { configurationManager.isSendEventsBlocked() } returns false
		coEvery { eventsQueue.getMaximumEventsToSend() } returns list
		coEvery { eventsQueue.hasEventsToSend() } returns false
		coEvery { apiService.reportEvents(any()) } returns ReportEventResult(ReportEventStatus.SUCCESS, 500)
		val eventService = EventsService(apiService, eventsQueue, eventsServiceTimer, configurationManager)
		eventService.addEvents(list)

		eventService.readyToFlush()

		coVerify(exactly = 1, timeout = 10000) { apiService.reportEvents(list) }
	}

	@Test
	fun readyToFlush_WhenFLushEventsSuccess_ThenEventsQueueCleared()
	{
		val list = mutableListOf<Event>()
		list.add(event)
		coEvery { configurationManager.isSendEventsBlocked() } returns false
		coEvery { eventsQueue.getMaximumEventsToSend() } returns list
		coEvery { eventsQueue.hasEventsToSend() } returns false
		coEvery { apiService.reportEvents(any()) } returns ReportEventResult(ReportEventStatus.SUCCESS, 500)
		val eventService = EventsService(apiService, eventsQueue, eventsServiceTimer, configurationManager)
		eventService.addEvent(event)

		eventService.readyToFlush()

		coVerify(exactly = 1, timeout = 10000) { eventsQueue.removeAll(any())}
	}

	@Test
	fun readyToFlush_WhenFlushAndNoNetwork_ThenEventsQueueNotCleared()
	{
		val list = mutableListOf<Event>()
		list.add(event)
		coEvery { configurationManager.isSendEventsBlocked() } returns false
		coEvery { eventsQueue.getMaximumEventsToSend() } returns list
		coEvery { eventsQueue.hasEventsToSend() } returns false
		coEvery { apiService.reportEvents(any()) } returns ReportEventResult(ReportEventStatus.ERROR_NETWORK, 500)
		val eventService = EventsService(apiService, eventsQueue, eventsServiceTimer, configurationManager)
		eventService.addEvent(event)

		eventService.readyToFlush()

		coVerify(exactly = 0, timeout = 10000) { eventsQueue.removeAll(any())}
	}

	@Test
	fun readyToFlush_WhenFLushEventsNoNetwork_ThenEventsQueueNotEmpty()
	{
		val list = mutableListOf<Event>()
		list.add(event)
		coEvery { configurationManager.isSendEventsBlocked() } returns false
		coEvery { eventsQueue.getMaximumEventsToSend() } returns list
		coEvery { eventsQueue.hasEventsToSend() } returns false
		coEvery { apiService.reportEvents(any()) } returns ReportEventResult(ReportEventStatus.ERROR_BAD_REQUEST, 500)
		coEvery { apiService.hasIdentify()} returns true

		val eventService = EventsService(apiService, eventsQueue, eventsServiceTimer, configurationManager)
		eventService.addEvent(event)

		eventService.readyToFlush()

		coVerify(exactly = 1, timeout = 10000) { eventsQueue.removeAll(any())}
	}
}
