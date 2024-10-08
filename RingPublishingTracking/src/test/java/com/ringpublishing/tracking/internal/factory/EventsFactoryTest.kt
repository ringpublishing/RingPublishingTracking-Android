/*
 *  Created by Grzegorz Małopolski on 10/11/21, 2:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.decorator.EventParam
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL

class EventsFactoryTest
{

	private val gson = GsonBuilder().create()

    @Before
    fun `Bypass android_util_Base64 to java_util_Base64`() {
        mockkStatic(Base64::class)
        val arraySlot = slot<ByteArray>()

        every {
            Base64.encodeToString(capture(arraySlot), Base64.NO_WRAP)
        } answers {
            java.util.Base64.getEncoder().encodeToString(arraySlot.captured)
        }
    }

	@Test
	fun createClickEvent_WhenNoParameters_ThenResultWithoutParameters()
	{
		val eventsFactory = EventsFactory(gson)

		val event = eventsFactory.createClickEvent()

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.CLICK.text, event.name)
		Assert.assertTrue(event.parameters.isEmpty())
	}

	@Test
	fun createClickEvent_WhenNoDomainParameter_ThenNoDomainInParameters()
	{
		val eventsFactory = EventsFactory(gson)

		val event = eventsFactory.createClickEvent("eventName")

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.CLICK.text, event.name)
		Assert.assertEquals("eventName", event.parameters[UserEventParam.SELECTED_ELEMENT_NAME.text])
		Assert.assertTrue(event.parameters[UserEventParam.TARGET_URL.text] == null)
	}

	@Test
	fun createClickEvent_WhenOnlyDomain_ThenResultWithOneParameter()
	{
		val eventsFactory = EventsFactory(gson)

		val event = eventsFactory.createClickEvent(publicationUrl = URL("https://domain.com"))

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.CLICK.text, event.name)

		Assert.assertTrue(event.parameters[UserEventParam.SELECTED_ELEMENT_NAME.text] == null)
		Assert.assertEquals("https://domain.com", event.parameters[UserEventParam.TARGET_URL.text])
	}

	@Test
	fun createClickEvent_WhenAllParameters_ThenCorrectResult()
	{
		val eventsFactory = EventsFactory(gson)

		val event = eventsFactory.createClickEvent("eventName", URL("https://domain.com"))

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.CLICK.text, event.name)
		Assert.assertEquals("eventName", event.parameters[UserEventParam.SELECTED_ELEMENT_NAME.text])
		Assert.assertEquals("https://domain.com", event.parameters[UserEventParam.TARGET_URL.text])
	}

	@Test
	fun createUserActionEvent_WhenNoParameters_ThenActionAndSubtypeInEvent()
	{
		val eventsFactory = EventsFactory(gson)

		val event = eventsFactory.createUserActionEvent("eventName", "actionSubtypeName")

		Assert.assertEquals(AnalyticsSystem.KROPKA_EVENTS.text, event.analyticsSystemName)
		Assert.assertEquals(EventType.USER_ACTION.text, event.name)
		Assert.assertEquals("eventName", event.parameters[UserEventParam.USER_ACTION_CATEGORY_NAME.text])
		Assert.assertEquals("actionSubtypeName", event.parameters[UserEventParam.USER_ACTION_SUBTYPE_NAME.text])
	}

	@Test
	fun createUserActionEvent_Parameters_ThenParametersInEvent()
	{
		val eventsFactory = EventsFactory(gson)
		val parameters = mutableMapOf("param1" to "vale1", "param2" to "value2")

		val event = eventsFactory.createUserActionEvent("eventName", "actionSubtypeName", parametersMap = parameters)

		Assert.assertEquals("{\"param1\":\"vale1\",\"param2\":\"value2\"}", event.parameters[UserEventParam.USER_ACTION_PAYLOAD.text])
	}

	@Test
	fun createUserActionEvent_StringParameters_ThenParametersInEvent()
	{
		val eventsFactory = EventsFactory(gson)
		val parameters = "{\"param1\":\"vale1\",\"param2\":\"value2\"}"

		val event = eventsFactory.createUserActionEvent("eventName", "actionSubtypeName", parameters)

		Assert.assertEquals(parameters, event.parameters[UserEventParam.USER_ACTION_PAYLOAD.text])
	}

	@Test
	fun createPageViewEvent_StringParameters_ThenParametersInEvent()
	{
		val eventsFactory = EventsFactory(gson)
		val contentMetadata = ContentMetadata(
			"publicationId",
			URL("https://domain.com"),
			"sourceSystemName",
			1,
			false,
			"my-unique-content-id-1234"
		)

		val event = eventsFactory.createPageViewEvent("publicationId", contentMetadata)

		Assert.assertEquals("PV_4,sourceSystemName,publicationId,1,f", event.parameters[UserEventParam.PAGE_VIEW_CONTENT_INFO.text])
		Assert.assertEquals(mockRdlcnEncodingNotPaid(), event.parameters[EventParam.MARKED_AS_PAID_DATA.text])
	}

	@Test
	fun createPageViewEvent_StringParametersPaidFor_ThenParametersInEvent()
	{
		val eventsFactory = EventsFactory(gson)
		val contentMetadata = ContentMetadata(
			"publicationId",
			URL("https://domain.com"),
			"source System_Name",
			1,
			true,
			"my-unique-content-id-1234"
		)

		val event = eventsFactory.createPageViewEvent("publicationId", contentMetadata)

		Assert.assertEquals("PV_4,source_System_Name,publicationId,1,t", event.parameters[UserEventParam.PAGE_VIEW_CONTENT_INFO.text])
        Assert.assertEquals(mockRdlcnEncodingPaid(), event.parameters[EventParam.MARKED_AS_PAID_DATA.text])
    }

    private fun mockRdlcnEncodingPaid() = encode(
        "{\"publication\":{\"premium\":true},\"source\":{\"id\":\"my-unique-content-id-1234\",\"system\":\"source System_Name\"}}"
    )

    private fun mockRdlcnEncodingNotPaid() = encode(
        "{\"publication\":{\"premium\":false},\"source\":{\"id\":\"my-unique-content-id-1234\",\"system\":\"sourceSystemName\"}}"
    )

    private fun encode(input: String): String {
        return Base64.encodeToString(
            input.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
    }
}
