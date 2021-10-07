/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.ConfigurationDelegate

internal class SecondaryIdDecorator(private val configuration: ConfigurationDelegate) : PrimaryIdDecorator(configuration)
{

	override fun decorate(event: Event)
	{
		event.add(EventParam.SECONDARY_ID, getSecondaryId())
	}

	private fun getSecondaryId(): String
	{
		with(configuration)
		{
			val generateNew = secondaryId == null && currentIsPartialView
			val newId = if (generateNew) parameterGenerator.generatePrimaryId() else primaryId ?: parameterGenerator.generatePrimaryId()
			secondaryId = newId
			return newId
		}
	}
}
