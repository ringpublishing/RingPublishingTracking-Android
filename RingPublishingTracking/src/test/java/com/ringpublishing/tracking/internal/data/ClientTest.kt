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
        val client = Client(ClientType(ClientPlatform.native_app, "text"))

        val result = client.toRdlc(Gson())

        Assert.assertEquals(
            "eyJjbGllbnQiOnsidHlwZSI6Im5hdGl2ZV9hcHAiLCJ2aWV3VHlwZSI6InRleHQifX0=",
            result,
        )
    }
}
