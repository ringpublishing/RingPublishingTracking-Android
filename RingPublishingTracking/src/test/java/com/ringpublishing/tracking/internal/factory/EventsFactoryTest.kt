/*
 *  Created by Grzegorz Małopolski on 10/11/21, 2:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.constants.EventType
import com.ringpublishing.tracking.internal.decorator.EventParam
import org.junit.Assert
import org.junit.Test
import java.net.URL

class EventsFactoryTest
{

	@Test
	fun createClickEvent_WhenNoParameters_ThenResultWithoutParameters()
	{
		val eventsFactory = EventsFactory()

		val event = eventsFactory.createClickEvent()

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.CLICK.text, event.name)
		Assert.assertTrue(event.parameters.isEmpty())
	}

	@Test
	fun createClickEvent_WhenNoDomainParameter_ThenNoDomainInParameters()
	{
		val eventsFactory = EventsFactory()

		val event = eventsFactory.createClickEvent("eventName")

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.CLICK.text, event.name)
		Assert.assertEquals("eventName", event.parameters[EventParam.SELECTED_ELEMENT_NAME.paramName])
		Assert.assertTrue(event.parameters[EventParam.TARGET_URL.paramName] == null)
	}

	@Test
	fun createClickEvent_WhenOnlyDomain_ThenResultWithOneParameter()
	{
		val eventsFactory = EventsFactory()

		val event = eventsFactory.createClickEvent(publicationUrl = URL("https://domain.com"))

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.CLICK.text, event.name)

		Assert.assertTrue(event.parameters[EventParam.SELECTED_ELEMENT_NAME.paramName] == null)
		Assert.assertEquals("https://domain.com", event.parameters[EventParam.TARGET_URL.paramName])
	}

	@Test
	fun createClickEvent_WhenAllParameters_ThenCorrectResult()
	{
		val eventsFactory = EventsFactory()

		val event = eventsFactory.createClickEvent("eventName", URL("https://domain.com"))

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.CLICK.text, event.name)
		Assert.assertEquals("eventName", event.parameters[EventParam.SELECTED_ELEMENT_NAME.paramName])
		Assert.assertEquals("https://domain.com", event.parameters[EventParam.TARGET_URL.paramName])
	}
}
