/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.ConfigurationDelegate

internal open class PrimaryIdDecorator(private val configurationDelegate: ConfigurationDelegate) : BaseDecorator()
{

	override fun decorate(event: Event)
	{
		event.add(EventParam.PRIMARY_ID, getPrimaryId())
	}

	private fun getPrimaryId(): Long
	{
		if (configurationDelegate.primaryId == 0L)
		{
			configurationDelegate.primaryId = parameterGenerator.generatePrimaryId()
		}
		return configurationDelegate.primaryId
	}
}
