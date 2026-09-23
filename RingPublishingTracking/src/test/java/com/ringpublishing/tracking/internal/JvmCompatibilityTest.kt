package com.ringpublishing.tracking.internal

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.delegate.RingPublishingTrackingKeepAliveDataSource
import com.ringpublishing.tracking.internal.factory.EventsFactory
import org.junit.Assert
import org.junit.Test

internal class JvmCompatibilityTest
{
    @Test
    fun legacyClientAndPageViewFactoryDescriptorsRemainAvailable()
    {
        val clientTypeClass = Class.forName("com.ringpublishing.tracking.internal.data.ClientType")
        val clientPlatformClass = Class.forName("com.ringpublishing.tracking.internal.data.ClientPlatform")
        val clientClass = Class.forName("com.ringpublishing.tracking.internal.data.Client")

        clientTypeClass.getConstructor(clientPlatformClass)
        clientClass.getConstructor(clientTypeClass)
        Assert.assertEquals(clientTypeClass, clientClass.getMethod("getClient").returnType)
        EventsFactory::class.java.getDeclaredMethod(
            "createPageViewEvent",
            String::class.java,
            ContentMetadata::class.java,
        )
        EventsFactory::class.java.getDeclaredMethod(
            "createPageViewEvent\$default",
            EventsFactory::class.java,
            String::class.java,
            ContentMetadata::class.java,
            Int::class.javaPrimitiveType,
            Any::class.java,
        )
    }

    @Test
    fun contentPageViewApiKeepsLegacyDescriptorAndUsesIosAlignedNewParameterOrder()
    {
        val apiClass = Class.forName(
            "com.ringpublishing.tracking.RingPublishingTracking_ContentPerformanceMonitorKt"
        )
        val contentViewTypeClass = Class.forName("com.ringpublishing.tracking.data.ContentViewType")

        apiClass.getDeclaredMethod(
            "reportContentPageView",
            RingPublishingTracking::class.java,
            ContentMetadata::class.java,
            ContentPageViewSource::class.java,
            List::class.java,
            Boolean::class.javaPrimitiveType,
            RingPublishingTrackingKeepAliveDataSource::class.java,
        )
        apiClass.getDeclaredMethod(
            "reportContentPageView",
            RingPublishingTracking::class.java,
            ContentMetadata::class.java,
            contentViewTypeClass,
            ContentPageViewSource::class.java,
            List::class.java,
            Boolean::class.javaPrimitiveType,
            RingPublishingTrackingKeepAliveDataSource::class.java,
        )
    }
}
