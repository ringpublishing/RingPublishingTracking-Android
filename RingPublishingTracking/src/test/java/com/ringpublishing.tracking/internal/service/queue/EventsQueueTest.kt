package com.ringpublishing.tracking.internal.service.queue

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.Constants
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EventsQueueTest
{

	@MockK
	internal lateinit var eventSizeCalculator: EventSizeCalculator

	@MockK
	lateinit var event: Event

	@MockK
	lateinit var event2: Event

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		RingPublishingTracking.setDebugMode(true)
	}

	@Test
	fun add_EnoElement_NoEventsToSend()
	{
		val eventsQueue = EventsQueue(eventSizeCalculator)
		Assert.assertTrue(!eventsQueue.hasEventsToSend())
	}

	@Test
	fun add_AddOneElement_HaveEventsToSend()
	{
		val eventsQueue = EventsQueue(eventSizeCalculator)
		eventsQueue.add(event)
		Assert.assertTrue(eventsQueue.hasEventsToSend())
	}

	@Test
	fun add_AddThreeElement_HaveEventsToSend()
	{
		val eventsQueue = EventsQueue(eventSizeCalculator)
		eventsQueue.add(event)
		eventsQueue.add(event2)
		Assert.assertTrue(eventsQueue.hasEventsToSend())
	}

	@Test
	fun getMaximumEventsToSend_EventSizeIsToBig_NoEventsToSend()
	{

		every { eventSizeCalculator.getSizeInBytes(event) } returns Constants.maxEventSize
		every { eventSizeCalculator.getSizeInBytes(event2) } returns Constants.maxEventSize

		val eventsQueue = EventsQueue(eventSizeCalculator)

		eventsQueue.add(event)
		eventsQueue.add(event2)

		val maximumEventsToSend = eventsQueue.getMaximumEventsToSend()
		Assert.assertTrue(maximumEventsToSend.isEmpty())
	}

	@Test
	fun getMaximumEventsToSend_EventSizeOk_TwoEventsToSend()
	{
		every { eventSizeCalculator.getSizeInBytes(event) } returns 10
		every { eventSizeCalculator.getSizeInBytes(event2) } returns 10
		every { eventSizeCalculator.isLowerThanMaxRequestSize(any(), any()) } returns true
		every { eventSizeCalculator.available(any()) } returns Constants.maxEventSize

		val eventsQueue = EventsQueue(eventSizeCalculator)

		eventsQueue.add(event)
		eventsQueue.add(event2)

		val maximumEventsToSend = eventsQueue.getMaximumEventsToSend()
		Assert.assertTrue(maximumEventsToSend.size == 2)
	}

	@Test
	fun getMaximumEventsToSend_EventSizeOkButBiggerTahRequestSize_NoEventsToSend()
	{
		every { eventSizeCalculator.getSizeInBytes(event) } returns 10
		every { eventSizeCalculator.getSizeInBytes(event2) } returns 10
		every { eventSizeCalculator.isLowerThanMaxRequestSize(any(), any()) } returns false
		every { eventSizeCalculator.available(any()) } returns Constants.maxEventSize

		val eventsQueue = EventsQueue(eventSizeCalculator)

		eventsQueue.add(event)
		eventsQueue.add(event2)

		val maximumEventsToSend = eventsQueue.getMaximumEventsToSend()
		Assert.assertTrue(maximumEventsToSend.isEmpty())
	}

	@Test
	fun removeAll_AddTwoElementThenRemove_NoEventsToSend()
	{
		val eventsQueue = EventsQueue(eventSizeCalculator)
		eventsQueue.add(event)
		eventsQueue.add(event2)
		val list = mutableListOf<Event>()
		list.add(event)
		list.add(event2)
		eventsQueue.removeAll(list)
		Assert.assertTrue(!eventsQueue.hasEventsToSend())
	}

	@Test
	fun removeAll_AddTwoElementThenRemoveOne_HaveEventsToSend()
	{
		val eventsQueue = EventsQueue(eventSizeCalculator)
		eventsQueue.add(event)
		eventsQueue.add(event2)
		val list = mutableListOf<Event>()
		list.add(event)
		eventsQueue.removeAll(list)
		Assert.assertTrue(eventsQueue.hasEventsToSend())
	}

	@Test
	fun removeAll_AddTwoElementThenRemoveTwo_NoEventsToSend()
	{
		val eventsQueue = EventsQueue(eventSizeCalculator)
		eventsQueue.add(event)
		eventsQueue.add(event2)
		val list = mutableListOf<Event>()
		list.add(event)
		list.add(event2)
		eventsQueue.removeAll(list)
		Assert.assertFalse(eventsQueue.hasEventsToSend())
	}
}
