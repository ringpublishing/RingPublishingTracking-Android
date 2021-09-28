package com.ringpublishing.tracking.internal.device

import android.content.res.Configuration
import android.content.res.Resources
import java.util.UUID
import kotlin.math.sqrt

internal class DeviceInfo(private val resources: Resources)
{

    private val tabletSizeMinInch = 7
    private val screenLayout = resources.configuration.screenLayout

    fun isTablet() = isLargeLayoutSize() && isDeviceResolutionGreaterThanTablets()

    private fun isLargeLayoutSize(): Boolean
    {
        return screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    private fun isDeviceResolutionGreaterThanTablets(): Boolean
    {
        val displayMetrics = resources.displayMetrics
        val yInches = displayMetrics.heightPixels / displayMetrics.ydpi
        val xInches = displayMetrics.widthPixels / displayMetrics.xdpi
        val diagonalInches = sqrt((xInches * xInches + yInches * yInches).toDouble())
        return diagonalInches >= tabletSizeMinInch
    }

    fun getDeviceId(): String
    {
        return UUID.randomUUID().toString()
    }
}
