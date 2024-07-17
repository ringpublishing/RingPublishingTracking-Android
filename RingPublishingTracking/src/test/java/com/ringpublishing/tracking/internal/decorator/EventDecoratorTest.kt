/*
 *  Created by Grzegorz Małopolski on 10/29/21, 10:29 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.app.Application
import android.content.SharedPreferences
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.api.response.ArtemisIdResponse
import com.ringpublishing.tracking.internal.api.response.Id
import com.ringpublishing.tracking.internal.api.response.User
import com.ringpublishing.tracking.internal.data.UserData
import com.ringpublishing.tracking.internal.device.WindowSizeInfo
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class EventDecoratorTest
{

	@MockK
	lateinit var configurationManager: ConfigurationManager

    @MockK
    lateinit var apiRepository: ApiRepository

	@MockK
	lateinit var application: Application

	@MockK
	lateinit var windowSizeInfo: WindowSizeInfo

	@MockK
	lateinit var screenSizeInfo: ScreenSizeInfo

	@MockK
	lateinit var event: Event

	@MockK
	lateinit var sharedPreferences: SharedPreferences

	@MockK
	lateinit var userData: UserData

    @MockK
    lateinit var artemisIdResponse: ArtemisIdResponse

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

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
	fun decorate()
	{
        mockDefaultParameters()

        every { userData.userId } returns "12345"
		every { userData.emailMd5 } returns "1234"
		every { userData.ssoName } returns "RingPublishingTrackingSSO"
		every { userData.isActiveSubscriber } returns true
        every { apiRepository.readArtemisId() } returns artemisIdResponse
        every { artemisIdResponse.user } returns mockArtemisIdUser()

		val eventDecorator = EventDecorator(configurationManager, apiRepository, Gson(), windowSizeInfo, screenSizeInfo)
		val eventDecorated = eventDecorator.decorate(event)

		with(eventDecorated)
		{
            Assert.assertEquals("name", this.name)
			Assert.assertEquals("primaryId", this.parameters["IP"])
			Assert.assertEquals("secondaryId", this.parameters["IV"])
			Assert.assertEquals("tenantId", this.parameters["TID"])
			Assert.assertEquals("area", this.parameters["DA"])
			Assert.assertEquals("1x1x24", this.parameters["CS"])
			Assert.assertEquals("1x1", this.parameters["CW"])
			Assert.assertEquals("contentUrl", this.parameters["DU"])
			Assert.assertEquals("structurePath", this.parameters["DV"])
			Assert.assertEquals("referrer", this.parameters["DR"])
			Assert.assertEquals(mockRdluArtemisSsoEncodingWithSubscription(), this.parameters["RDLU"])
		}
	}

    @Test
    fun decorateEvent_WithSSO_WithNoArtemisId_WithNoSubscription()
    {
        mockDefaultParameters()

        every { userData.userId } returns "12345"
        every { userData.emailMd5 } returns "1234"
        every { userData.ssoName } returns "RingPublishingTrackingSSO"
        every { userData.isActiveSubscriber } returns false
        every { apiRepository.readArtemisId() } returns null
        every { artemisIdResponse.user } returns null

        val eventDecorator = EventDecorator(configurationManager, apiRepository, Gson(), windowSizeInfo, screenSizeInfo)
        val eventDecorated = eventDecorator.decorate(event)

        with(eventDecorated)
        {
            Assert.assertEquals(mockRdluSsoEncoding(), this.parameters["RDLU"])
        }
    }

    @Test
    fun decorateEvent_WithSSO_WithNoArtemisId_WithSubscription()
    {
        mockDefaultParameters()

        every { userData.userId } returns "12345"
        every { userData.emailMd5 } returns "1234"
        every { userData.ssoName } returns "RingPublishingTrackingSSO"
        every { userData.isActiveSubscriber } returns true
        every { apiRepository.readArtemisId() } returns null
        every { artemisIdResponse.user } returns null

        val eventDecorator = EventDecorator(configurationManager, apiRepository, Gson(), windowSizeInfo, screenSizeInfo)
        val eventDecorated = eventDecorator.decorate(event)

        with(eventDecorated)
        {
            Assert.assertEquals(mockRdluSsoEncodingWithSubscription(), this.parameters["RDLU"])
        }
    }

    @Test
    fun decorateEvent_WithArtemisId_WithNOSSO_WithNoSubscription()
    {
        mockDefaultParameters()

        every { userData.userId } returns null
        every { userData.emailMd5 } returns null
        every { userData.ssoName } returns null
        every { userData.isActiveSubscriber } returns null
        every { apiRepository.readArtemisId() } returns artemisIdResponse
        every { artemisIdResponse.user } returns mockArtemisIdUser()

        val eventDecorator = EventDecorator(configurationManager, apiRepository, Gson(), windowSizeInfo, screenSizeInfo)
        val eventDecorated = eventDecorator.decorate(event)

        with(eventDecorated)
        {
            Assert.assertEquals(mockRdluArtemisEncoding(), this.parameters["RDLU"])
        }
    }

    @Test
    fun decorateEvent_WithArtemisId_WithNOSSO_WithSubscription()
    {
        mockDefaultParameters()

        every { userData.userId } returns null
        every { userData.emailMd5 } returns null
        every { userData.ssoName } returns null
        every { userData.isActiveSubscriber } returns true
        every { apiRepository.readArtemisId() } returns artemisIdResponse
        every { artemisIdResponse.user } returns mockArtemisIdUser()

        val eventDecorator = EventDecorator(configurationManager, apiRepository, Gson(), windowSizeInfo, screenSizeInfo)
        val eventDecorated = eventDecorator.decorate(event)

        with(eventDecorated)
        {
            Assert.assertEquals(mockRdluArtemisEncodingWithSubscription(), this.parameters["RDLU"])
        }
    }

    private fun mockDefaultParameters() {
        every { application.packageName } returns "package"
        every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { configurationManager.primaryId } returns "primaryId"
        every { configurationManager.secondaryId } returns "secondaryId"
        every { configurationManager.getTenantId() } returns "tenantId"
        every { configurationManager.getSiteArea() } returns "area"
        every { configurationManager.getUserData() } returns userData
        every { configurationManager.currentContentUrl } returns "contentUrl"
        every { configurationManager.getFullStructurePath() } returns "structurePath"
        every { configurationManager.currentReferrer } returns "referrer"
        every { screenSizeInfo.getScreenSizeDpString() } returns "1x1"
        every { windowSizeInfo.getWindowSizeDpString() } returns "1x1"
        every { sharedPreferences.getString(any(), any()) } returns "preference"
        every { event.name } returns "name"
        every { event.parameters } returns mutableMapOf()
    }

    private fun mockArtemisIdUser() = User(
        id = Id(
            real = "1234",
            model = "1234",
            models = Gson().fromJson("{\n\"ats_ri\": \"1234\"\n}", JsonElement::class.java)
        )
    )

    private fun mockRdluArtemisSsoEncoding() = mockRdluEncoding(
        "{\"id\":{\"artemis\":\"1234\",\"external\":{\"model\":\"1234\",\"models\":{\"ats_ri\":\"1234\"}}},\"sso\":{\"logged\":{\"id\":\"12345\",\"md5\":\"1234\"},\"name\":\"RingPublishingTrackingSSO\"}}"
    )

    private fun mockRdluArtemisSsoEncodingWithSubscription() = mockRdluEncoding(
        "{\"id\":{\"artemis\":\"1234\",\"external\":{\"model\":\"1234\",\"models\":{\"ats_ri\":\"1234\"}}},\"sso\":{\"logged\":{\"id\":\"12345\",\"md5\":\"1234\"},\"name\":\"RingPublishingTrackingSSO\"},\"type\":\"subscriber\"}"    )

    private fun mockRdluSsoEncoding() = mockRdluEncoding(
        "{\"sso\":{\"logged\":{\"id\":\"12345\",\"md5\":\"1234\"},\"name\":\"RingPublishingTrackingSSO\"}}"
    )

    private fun mockRdluSsoEncodingWithSubscription() = mockRdluEncoding(
        "{\"sso\":{\"logged\":{\"id\":\"12345\",\"md5\":\"1234\"},\"name\":\"RingPublishingTrackingSSO\"},\"type\":\"subscriber\"}"
    )

    private fun mockRdluArtemisEncoding() = mockRdluEncoding(
        "{\"id\":{\"artemis\":\"1234\",\"external\":{\"model\":\"1234\",\"models\":{\"ats_ri\":\"1234\"}}}}"
    )

    private fun mockRdluArtemisEncodingWithSubscription() = mockRdluEncoding(
        "{\"id\":{\"artemis\":\"1234\",\"external\":{\"model\":\"1234\",\"models\":{\"ats_ri\":\"1234\"}}},\"type\":\"subscriber\"}"
    )

    private fun mockRdluEncoding(input: String): String {
        return Base64.encodeToString(
            input.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
    }
}
