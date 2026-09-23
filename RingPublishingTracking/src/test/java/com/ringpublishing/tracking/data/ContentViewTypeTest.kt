package com.ringpublishing.tracking.data

import org.junit.Assert
import org.junit.Test

internal class ContentViewTypeTest {

    @Test
    fun values_WhenRead_ThenMatchAnalyticsContract() {
        Assert.assertEquals(
            listOf("text", "tts", "smartshort"),
            ContentViewType.values().map { it.value },
        )
    }
}
