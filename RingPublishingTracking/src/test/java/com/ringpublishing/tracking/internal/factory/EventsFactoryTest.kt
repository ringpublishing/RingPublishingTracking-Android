/*
 *  Created by Grzegorz Małopolski on 10/11/21, 2:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
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
		Assert.assertEquals("eventName", event.parameters[UserEventParam.SELECTED_ELEMENT_NAME.text])
		Assert.assertTrue(event.parameters[UserEventParam.TARGET_URL.text] == null)
	}

	@Test
	fun createClickEvent_WhenOnlyDomain_ThenResultWithOneParameter()
	{
		val eventsFactory = EventsFactory()

		val event = eventsFactory.createClickEvent(publicationUrl = URL("https://domain.com"))

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.CLICK.text, event.name)

		Assert.assertTrue(event.parameters[UserEventParam.SELECTED_ELEMENT_NAME.text] == null)
		Assert.assertEquals("https://domain.com", event.parameters[UserEventParam.TARGET_URL.text])
	}

	@Test
	fun createClickEvent_WhenAllParameters_ThenCorrectResult()
	{
		val eventsFactory = EventsFactory()

		val event = eventsFactory.createClickEvent("eventName", URL("https://domain.com"))

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.CLICK.text, event.name)
		Assert.assertEquals("eventName", event.parameters[UserEventParam.SELECTED_ELEMENT_NAME.text])
		Assert.assertEquals("https://domain.com", event.parameters[UserEventParam.TARGET_URL.text])
	}
}
