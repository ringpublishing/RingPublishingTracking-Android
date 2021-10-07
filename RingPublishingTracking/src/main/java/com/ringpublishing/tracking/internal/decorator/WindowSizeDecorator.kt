/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.content.Context
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import com.ringpublishing.tracking.data.Event

internal class WindowSizeDecorator(context: Context) : DeviceScreenDecorator(context)
{

	private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

	override fun decorate(event: Event)
	{
		event.add(EventParam.WINDOW_SIZE, getWindowSizeString())
	}

	private fun getWindowSizeString(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
	{
		with(windowManager.currentWindowMetrics)
		{
			val insets = windowInsets.getInsets(WindowInsets.Type.systemBars())
			with(bounds)
			{
				buildSizeString(right - left - insets.left - insets.right, bottom - top - insets.bottom - insets.top)
			}
		}
	} else getScreenSizeString()
}
