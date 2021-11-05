/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo

internal open class DeviceScreenDecorator(private val screenSizeInfo: ScreenSizeInfo) : BaseDecorator()
{

	override fun decorate(event: Event)
	{
		event.add(EventParam.SCREEN_SIZE, "${screenSizeInfo.getScreenSizeDpString()}x${Constants.mobileDepth}")
	}
}
