/*
 *  Created by Marcin Nowacki on 9/7/26, 3:00 PM
 * Copyright © 2026 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class SessionIdentifierDecoratorTest
{

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenEventDecorated_ThenParametersHaveSessionId()
	{
		val decorator = SessionIdentifierDecorator()

		val event = Event()
		decorator.decorate(event)

		val sessionId = event.parameters["IS"] as String?

		Assert.assertNotNull(sessionId)
		Assert.assertEquals(24, sessionId?.length)
	}

	@Test
	fun decorate_WhenNewSessionNotStarted_ThenValueStaysConstant()
	{
		val decorator = SessionIdentifierDecorator()

		val firstEvent = Event()
		decorator.decorate(firstEvent)

		val secondEvent = Event()
		decorator.decorate(secondEvent)

		Assert.assertEquals(firstEvent.parameters["IS"], secondEvent.parameters["IS"])
	}

	@Test
	fun decorate_WhenNewSessionStarted_ThenValueChanges()
	{
		val decorator = SessionIdentifierDecorator()

		val firstEvent = Event()
		decorator.decorate(firstEvent)

		decorator.startNewSession()

		val secondEvent = Event()
		decorator.decorate(secondEvent)

		Assert.assertNotEquals(firstEvent.parameters["IS"], secondEvent.parameters["IS"])
	}

	@Test
	fun decorate_WhenTwoDecoratorsCreated_ThenSessionIdsAreDifferent()
	{
		val decorator1 = SessionIdentifierDecorator()
		Thread.sleep(1000)
		val decorator2 = SessionIdentifierDecorator()

		val event1 = Event()
		decorator1.decorate(event1)

		val event2 = Event()
		decorator2.decorate(event2)

		Assert.assertNotEquals(event1.parameters["IS"], event2.parameters["IS"])
	}

	@Test
	fun decorate_WhenEventDecorated_ThenSessionIdIsAllDigits()
	{
		val decorator = SessionIdentifierDecorator()

		val event = Event()
		decorator.decorate(event)

		val sessionId = event.parameters["IS"] as String?

		Assert.assertNotNull(sessionId)
		Assert.assertTrue(sessionId!!.all { it.isDigit() })
	}

	@Test
	fun decorate_WhenEventDecorated_ThenSessionIdStartsWithCurrentTimestamp()
	{
		val dateFormatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
		val expectedTimestamp = dateFormatter.format(Date())

		val decorator = SessionIdentifierDecorator()

		val event = Event()
		decorator.decorate(event)

		val sessionId = event.parameters["IS"] as String?

		Assert.assertNotNull(sessionId)
		Assert.assertTrue(sessionId!!.startsWith(expectedTimestamp.substring(0, 12)))
	}
}
