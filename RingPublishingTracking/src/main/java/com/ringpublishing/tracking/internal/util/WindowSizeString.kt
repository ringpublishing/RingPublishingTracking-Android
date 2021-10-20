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

	private fun buildSizeString(width: Int, height: Int): String
	{
		val dpWidth = (width / metrics.density).roundToInt()
		val dpHeight = (height / metrics.density).roundToInt()
		return "${dpWidth}x$dpHeight"
	}

	fun getWindowSizeDp(): WindowSize
	{
		val dpWidth = (metrics.widthPixels / metrics.density).roundToInt()
		val dpHeight = (metrics.heightPixels / metrics.density).roundToInt()
		return WindowSize(dpWidth, dpHeight)
	}

	fun getScreenSizeString() = buildSizeString(metrics.widthPixels, metrics.heightPixels)
}
