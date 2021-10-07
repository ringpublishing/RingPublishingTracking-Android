/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.ConfigurationDelegate

internal class ReferrerDecorator(private val configurationDelegate: ConfigurationDelegate) : BaseDecorator()
{
	private val contentReferrer get() = configurationDelegate.currentReferrer

	override fun decorate(event: Event)
	{
		contentReferrer?.let {
			event.add(EventParam.CONTENT_REFERER, contentReferrer)
		}
	}
}
