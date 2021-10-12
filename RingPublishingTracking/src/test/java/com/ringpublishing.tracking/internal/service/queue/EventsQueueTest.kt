package com.ringpublishing.tracking.internal.service.queue

import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL

class EventsQueueTest
{

	@MockK
	internal lateinit var eventSizeCalculator: EventSizeCalculator

	@MockK
	internal lateinit var apiRepository: ApiRepository

	@MockK
	internal lateinit var userRepository: UserRepository

	@MockK
	internal lateinit var identifyResponse: IdentifyResponse

	@MockK
	internal lateinit var user: User

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

	@Test
	fun add_EventWithURLParameter_ThenHaveOneEventToSend()
	{
		coEvery { apiRepository.readIdentify() } returns identifyResponse
		coEvery { apiRepository.readIdentifyRequestDate() } returns null
		coEvery { userRepository.buildUser() } returns user
		val realEventSizeCalculator = EventSizeCalculator(GsonBuilder().create(), apiRepository, userRepository)
		val eventsQueue = EventsQueue(realEventSizeCalculator)
		val failParameters = mutableMapOf<String, Any>("param" to URL("https://domain.com"))
		val failEvent = Event(parameters = failParameters)
		eventsQueue.add(failEvent)

		val events = eventsQueue.getMaximumEventsToSend()

		Assert.assertTrue(events.size == 1)
	}

	@Test
	fun add_DifferentParameterTypesToMap_ThenHaveCorrectParsedEvent()
	{
		coEvery { apiRepository.readIdentify() } returns identifyResponse
		coEvery { apiRepository.readIdentifyRequestDate() } returns null
		coEvery { userRepository.buildUser() } returns user
		val realEventSizeCalculator = EventSizeCalculator(GsonBuilder().create(), apiRepository, userRepository)
		val eventsQueue = EventsQueue(realEventSizeCalculator)

		val failParameters = mutableMapOf("paramString" to "text", "paramInt" to 1, "paramLong" to 1L, "paramList" to listOf("text"), "paramMap" to mapOf("key" to 1))

		val failEvent = Event(parameters = failParameters)
		eventsQueue.add(failEvent)

		val events = eventsQueue.getMaximumEventsToSend()

		Assert.assertTrue(events.size == 1)
	}
}
