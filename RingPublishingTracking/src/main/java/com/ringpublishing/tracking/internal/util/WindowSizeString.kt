/*
 *  Created by Grzegorz Małopolski on 10/15/21, 1:54 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.util

import android.content.Context
import android.util.DisplayMetrics
import com.ringpublishing.tracking.internal.data.WindowSize
import kotlin.math.roundToInt

internal class WindowSizeString(context: Context)
{

	private val metrics: DisplayMetrics = context.resources.displayMetrics

	private fun buildSizeString(windowSize: WindowSize) = "${windowSize.width}x${windowSize.height}"

	fun getWindowSizePxFromMetrics() = WindowSize(metrics.widthPixels, metrics.heightPixels)

	fun getWindowSizeDp(windowSize: WindowSize) = WindowSize(getSizeDp(windowSize.width), getSizeDp(windowSize.height))

	fun getScreenSizeDpString() = buildSizeString(getWindowSizeDp(WindowSize(metrics.widthPixels, metrics.heightPixels)))

	fun getWindowSizeDpString(windowSize: WindowSize) = buildSizeString(getWindowSizeDp(windowSize))

	fun getSizeDp(sizePx: Int) = (sizePx / metrics.density).roundToInt()
}
