/*
 *  Created by Grzegorz Małopolski on 10/11/21, 2:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

import android.util.Base64
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.internal.decodeRdlcn
import com.ringpublishing.tracking.internal.decodeRdlcnObjectId
import com.ringpublishing.tracking.internal.mockAndroidBase64Encoding
import com.ringpublishing.tracking.internal.constants.AnalyticsSystem
import com.ringpublishing.tracking.internal.decorator.EventParam
import com.ringpublishing.tracking.internal.rdlcnObjectIdVectors
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL

class EventsFactoryTest
{

	private val gson = GsonBuilder().create()

    @Before
    fun `Bypass android_util_Base64 to java_util_Base64`() {
        mockAndroidBase64Encoding()
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
			"my-unique-content-id-1234",
            "my-unique-content-space-uuid-1234"
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
			"my-unique-content-id-1234",
            "my-unique-content-space-uuid-1234"
        )

		val event = eventsFactory.createPageViewEvent("publicationId", contentMetadata)

		Assert.assertEquals("PV_4,source_System_Name,publicationId,1,t", event.parameters[UserEventParam.PAGE_VIEW_CONTENT_INFO.text])
        Assert.assertEquals(mockRdlcnEncodingPaid(), event.parameters[EventParam.MARKED_AS_PAID_DATA.text])
    }

    @Test
    fun createPageViewEvent_WhenClientDataProvided_ThenEventOwnsRdlc()
    {
        val event = EventsFactory(gson).createPageViewEvent(
            contentIdentifier = null,
            contentMetadata = null,
            clientData = "encoded-client-data",
        )

        Assert.assertEquals("encoded-client-data", event.parameters[EventParam.CLIENT_ID.text])
    }

    @Test
    fun createPageViewEvent_WhenViewTypeMissing_ThenRdlcIsLeftToDecorator()
    {
        val event = EventsFactory(gson).createPageViewEvent()

        Assert.assertFalse(event.parameters.containsKey(EventParam.CLIENT_ID.text))
    }

    @Test
    fun createPageViewEvent_WhenContentIdIsUuid_ThenRdlcnStartsWithCanonicalObject()
    {
        val contentMetadata = sampleContentMetadata(contentId = "  E0BE23E3-A100-4D4F-A347-0635DE46BFC4  ")

        val event = EventsFactory(gson).createPageViewEvent(contentMetadata.contentId, contentMetadata)

        Assert.assertEquals(
            "{\"object\":{\"id\":\"e0be23e3-a100-4d4f-a347-0635de46bfc4\"},\"publication\":{\"premium\":false}," +
                    "\"source\":{\"id\":\"my-unique-content-space-uuid-1234\",\"system\":\"sourceSystemName\"}}",
            event.decodeRdlcn()
        )
    }

    @Test
    fun createPageViewEvent_WhenSharedContentIdVectors_ThenRdlcnObjectIdIsCanonical()
    {
        rdlcnObjectIdVectors.forEach { (contentId, expectedObjectId) ->
            val contentMetadata = sampleContentMetadata(contentId)

            val event = EventsFactory(gson).createPageViewEvent(contentMetadata.contentId, contentMetadata)

            Assert.assertEquals("contentId '$contentId'", expectedObjectId, event.decodeRdlcnObjectId())
        }
    }

    @Test
    fun createPageViewEvent_WhenContentIdPaddedWithAsciiWhitespace_ThenRdlcnObjectIdIsTrimmed()
    {
        val contentId = " \t\n\r\u000B\u000Ce0be23e3-a100-4d4f-a347-0635de46bfc4\u000C\u000B\r\n\t "
        val contentMetadata = sampleContentMetadata(contentId)

        val event = EventsFactory(gson).createPageViewEvent(contentMetadata.contentId, contentMetadata)

        Assert.assertEquals("e0be23e3-a100-4d4f-a347-0635de46bfc4", event.decodeRdlcnObjectId())
    }

    @Test
    fun createPageViewEvent_WhenContentIdPaddedWithWhitespaceOutsideTrimSet_ThenRdlcnHasNoObject()
    {
        listOf('\u00A0', '\u2003', '\u001F').forEach { padding ->
            val contentMetadata = sampleContentMetadata("${padding}e0be23e3-a100-4d4f-a347-0635de46bfc4$padding")

            val event = EventsFactory(gson).createPageViewEvent(contentMetadata.contentId, contentMetadata)

            Assert.assertNull("padding U+%04X".format(padding.code), event.decodeRdlcnObjectId())
        }
    }

    private fun sampleContentMetadata(contentId: String) = ContentMetadata(
        publicationId = "publicationId",
        publicationUrl = URL("https://domain.com"),
        sourceSystemName = "sourceSystemName",
        paidContent = false,
        contentId = contentId,
        contentSpaceUuid = "my-unique-content-space-uuid-1234"
    )

    private fun mockRdlcnEncodingPaid() = encode(
        "{\"publication\":{\"premium\":true},\"source\":{\"id\":\"my-unique-content-space-uuid-1234\",\"system\":\"source System_Name\"}}"
    )

    private fun mockRdlcnEncodingNotPaid() = encode(
        "{\"publication\":{\"premium\":false},\"source\":{\"id\":\"my-unique-content-space-uuid-1234\",\"system\":\"sourceSystemName\"}}"
    )

    private fun encode(input: String): String {
        return Base64.encodeToString(
            input.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
    }
}
