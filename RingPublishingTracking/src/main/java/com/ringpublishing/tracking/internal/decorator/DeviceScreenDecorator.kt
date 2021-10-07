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

internal open class DeviceScreenDecorator(context: Context) : BaseDecorator()
{

	private val displayMetrics: DisplayMetrics = context.resources.displayMetrics

	override fun decorate(event: Event)
	{
		event.add(EventParam.SCREEN_SIZE, getScreenSizeString())
	}

	fun getScreenSizeString() = buildSizeString(displayMetrics.widthPixels, displayMetrics.heightPixels)

	fun buildSizeString(width: Int, height: Int) = "${width}x${height}x${Constants.mobileDepth}"
}
