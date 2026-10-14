package com.ringpublishing.tracking.internal.data

import com.google.gson.Gson
import com.ringpublishing.tracking.internal.mockAndroidBase64Encoding
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class ClientTest
{
    @Before
    fun `Bypass android_util_Base64 to java_util_Base64`()
    {
        mockAndroidBase64Encoding()
    }

    @Test
    fun toRdlc_WhenViewTypeProvided_ThenEncodesClientData()
    {
        val client = Client(ClientType(ClientPlatform.native_app), ViewType("text"))

        val result = client.toRdlc(Gson())

        Assert.assertEquals(
            "eyJjbGllbnQiOnsidHlwZSI6Im5hdGl2ZV9hcHAifSwidmlldyI6eyJ0eXBlIjoidGV4dCJ9fQ==",
            result,
        )
    }

    @Test
    fun toRdlc_WhenTrackingProvided_ThenEncodesTrackingAfterClient()
    {
        val client = Client(
            ClientType(ClientPlatform.native_app),
            tracking = ClientTracking(ClientTrackingEnv(stage = true)),
        )

        val result = client.toRdlc(Gson())

        Assert.assertEquals(
            "eyJjbGllbnQiOnsidHlwZSI6Im5hdGl2ZV9hcHAifSwidHJhY2tpbmciOnsiZW52Ijp7InN0YWdlIjp0cnVlfX19",
            result,
        )
    }

    @Test
    fun toRdlc_WhenViewTypeAndTrackingProvided_ThenEncodesTrackingAfterView()
    {
        val client = Client(
            ClientType(ClientPlatform.native_app),
            ViewType("text"),
            tracking = ClientTracking(ClientTrackingEnv(stage = true)),
        )

        val result = client.toRdlc(Gson())

        Assert.assertEquals(
            "eyJjbGllbnQiOnsidHlwZSI6Im5hdGl2ZV9hcHAifSwidmlldyI6eyJ0eXBlIjoidGV4dCJ9" +
                "LCJ0cmFja2luZyI6eyJlbnYiOnsic3RhZ2UiOnRydWV9fX0=",
            result,
        )
    }
}
