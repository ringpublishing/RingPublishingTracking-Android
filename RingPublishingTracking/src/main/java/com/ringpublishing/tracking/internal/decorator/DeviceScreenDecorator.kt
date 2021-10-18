/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.content.Context
import android.util.DisplayMetrics
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.Constants
import kotlin.math.roundToInt

internal open class DeviceScreenDecorator(context: Context) : BaseDecorator()
{

	private val displayMetrics: DisplayMetrics = context.resources.displayMetrics

	override fun decorate(event: Event)
	{
		event.add(EventParam.SCREEN_SIZE, "${getScreenSizeString()}x${Constants.mobileDepth}")
	}

	protected fun getScreenSizeString() = buildSizeString(displayMetrics.widthPixels, displayMetrics.heightPixels)

	protected fun buildSizeString(width: Int, height: Int): String
	{
		val dpWidth = (width / displayMetrics.density).roundToInt()
		val dpHeight = (height / displayMetrics.density).roundToInt()
		return "${dpWidth}x$dpHeight"
	}
}
