package com.ringpublishing.tracking.internal

import android.util.Base64
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.decorator.EventParam
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot

internal fun mockAndroidBase64Encoding()
{
    mockkStatic(Base64::class)
    val arraySlot = slot<ByteArray>()

    every {
        Base64.encodeToString(capture(arraySlot), Base64.NO_WRAP)
    } answers {
        java.util.Base64.getEncoder().encodeToString(arraySlot.captured)
    }
}

internal fun Event.decodeRdlc(): String
{
    val encodedRdlc = parameters[EventParam.CLIENT_ID.text] as String
    return String(java.util.Base64.getDecoder().decode(encodedRdlc))
}
