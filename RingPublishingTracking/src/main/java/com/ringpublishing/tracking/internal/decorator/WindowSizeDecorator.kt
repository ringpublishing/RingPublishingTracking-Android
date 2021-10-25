/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.device.WindowSizeInfo
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo

internal class WindowSizeDecorator(private val windowSizeInfo: WindowSizeInfo, screenSizeInfo: ScreenSizeInfo) : DeviceScreenDecorator(screenSizeInfo)
{

	override fun decorate(event: Event)
	{
		event.add(EventParam.WINDOW_SIZE, windowSizeInfo.getWindowSizeDpString())
	}
}
