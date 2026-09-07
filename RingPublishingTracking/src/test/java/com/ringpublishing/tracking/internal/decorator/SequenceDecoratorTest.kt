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

internal class SequenceDecoratorTest
{

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenEventDecoratedCalled_ThenValueIncrementsPerCall()
	{
		val decorator = SequenceDecorator()

		val firstEvent = Event()
		decorator.decorate(firstEvent)
		Assert.assertEquals(0, firstEvent.parameters["SQ"])

		decorator.eventDecorated()

		val secondEvent = Event()
		decorator.decorate(secondEvent)
		Assert.assertEquals(1, secondEvent.parameters["SQ"])

		decorator.eventDecorated()

		val thirdEvent = Event()
		decorator.decorate(thirdEvent)
		Assert.assertEquals(2, thirdEvent.parameters["SQ"])
	}

	@Test
	fun decorate_WhenEventDecoratedNotCalled_ThenValueStaysConstant()
	{
		val decorator = SequenceDecorator()

		val firstEvent = Event()
		decorator.decorate(firstEvent)
		Assert.assertEquals(0, firstEvent.parameters["SQ"])

		val secondEvent = Event()
		decorator.decorate(secondEvent)
		Assert.assertEquals(0, secondEvent.parameters["SQ"])
	}

	@Test
	fun decorate_WhenMaxValueReached_ThenValueWrapsToZero()
	{
		val decorator = SequenceDecorator(sequence = Int.MAX_VALUE)

		decorator.eventDecorated()

		val event = Event()
		decorator.decorate(event)
		Assert.assertEquals(0, event.parameters["SQ"])
	}
}
