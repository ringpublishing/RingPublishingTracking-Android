/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.ConfigurationManager

internal class SiteAreaDecorator(private val configurationManager: ConfigurationManager) : BaseDecorator()
{

	override fun decorate(event: Event)
	{
		event.add(EventParam.SITE_AREA, configurationManager.getSiteArea())
	}
}