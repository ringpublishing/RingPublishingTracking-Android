/*
 *  Created by Grzegorz Małopolski on 10/29/21, 10:22 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.decorator.EventDecorator
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.service.EventsService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

internal class EventsReporterTest
{

	@MockK
	lateinit var eventService: EventsService

	@MockK
	lateinit var eventDecorator: EventDecorator

	@MockK
	lateinit var event: Event

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun reportEvent_WhenReportEvent_ThenEventIsAddedToService()
	{
		every { eventDecorator.decorate(event) } returns event
		val eventsReporter = EventsReporter(eventService, eventDecorator)

		eventsReporter.reportEvent(event)

		verify { eventService.addEvent(event) }
	}

	@Test
	fun reportEvents()
	{
		every { eventDecorator.decorate(event) } returns event
		val eventsReporter = EventsReporter(eventService, eventDecorator)

		eventsReporter.reportEvents(listOf(event, event))

		verify{ eventService.addEvents(listOf(event, event)) }
	}
}
