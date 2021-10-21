/*
 *  Created by Grzegorz Małopolski on 10/15/21, 1:54 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.util

import android.content.res.Resources
import android.util.DisplayMetrics
import com.ringpublishing.tracking.internal.data.WindowSize
import kotlin.math.roundToInt

internal class WindowSizeString
{

	private val screenMetrics: DisplayMetrics = Resources.getSystem().displayMetrics

	private fun buildSizeString(windowSize: WindowSize) = "${windowSize.width}x${windowSize.height}"

	fun getScreenSizePxFromMetrics() = WindowSize(screenMetrics.widthPixels, screenMetrics.heightPixels)

	private fun getWindowSizeDp(windowSize: WindowSize) = WindowSize(getSizeDp(windowSize.width), getSizeDp(windowSize.height))

	fun getScreenSizeDpString() = buildSizeString(getWindowSizeDp(WindowSize(screenMetrics.widthPixels, screenMetrics.heightPixels)))

	fun getWindowSizeDpString(windowSize: WindowSize) = buildSizeString(getWindowSizeDp(windowSize))

	fun getSizeDp(sizePx: Int) = (sizePx / screenMetrics.density).roundToInt()
}
