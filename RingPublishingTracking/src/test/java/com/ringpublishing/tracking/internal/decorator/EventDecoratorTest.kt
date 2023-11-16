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

		every { userData.userId } returns "12345"
		every { userData.emailMd5 } returns "1234"
		every { userData.ssoName } returns "RingPublishingTrackingSSO"

		every { screenSizeInfo.getScreenSizeDpString() } returns "1x1"
		every { windowSizeInfo.getWindowSizeDpString() } returns "1x1"

		every { sharedPreferences.getString(any(), any()) } returns "preference"

		every { event.name } returns "name"

        every { apiRepository.readArtemisId() } returns artemisIdResponse
        every { artemisIdResponse.user } returns mockArtemisIdUser()

		val parameters = mutableMapOf<String, Any>()

		every { event.parameters } returns parameters

		val eventDecorator = EventDecorator(configurationManager, apiRepository, Gson(), windowSizeInfo, screenSizeInfo)

		val eventDecorated = eventDecorator.decorate(event)

		Assert.assertEquals("name", eventDecorated.name)

		with(eventDecorated)
		{
			Assert.assertEquals("primaryId", parameters["IP"])
			Assert.assertEquals("secondaryId", parameters["IV"])
			Assert.assertEquals("tenantId", parameters["TID"])
			Assert.assertEquals("area", parameters["DA"])
			Assert.assertEquals("1x1x24", parameters["CS"])
			Assert.assertEquals("1x1", parameters["CW"])
			Assert.assertEquals("contentUrl", parameters["DU"])
			Assert.assertEquals("structurePath", parameters["DV"])
			Assert.assertEquals("referrer", parameters["DR"])
			Assert.assertEquals(mockRdluEncoding(), parameters["RDLU"])
		}
	}

    private fun mockArtemisIdUser() = User(
        id = Id(
            real = "1234",
            model = "1234",
            models = Gson().fromJson("{\n\"ats_ri\": \"1234\"\n}", JsonElement::class.java)
        )
    )

    private fun mockRdluEncoding(): String {
        val jsonUser =
            "{\"id\":{\"artemis\":\"1234\",\"external\":{\"model\":\"1234\",\"models\":{\"ats_ri\":\"1234\"}}},\"sso\":{\"logged\":{\"id\":\"12345\",\"md5\":\"1234\"},\"name\"" +
                    ":\"RingPublishingTrackingSSO\"}}"
        return Base64.encodeToString(
            jsonUser.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
    }
}
