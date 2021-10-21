/*
 *  Created by Grzegorz Małopolski on 10/1/21, 1:11 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.app.Application
import com.google.gson.Gson
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.device.WindowSizeInfo
import com.ringpublishing.tracking.internal.log.Logger

internal class EventDecorator(
    configurationManager: ConfigurationManager,
    application: Application,
    gson: Gson,
    windowSizeInfo: WindowSizeInfo
)
{
	private val decorators = mutableListOf<Decorator>()

	init
	{
		with(decorators)
		{
			add(PrimaryIdDecorator(configurationManager))
			add(SecondaryIdDecorator(configurationManager))
			add(UserSsoDataDecorator(configurationManager, gson))
			add(TenantIdDecorator(configurationManager))
			add(SiteAreaDecorator(configurationManager))
			add(DeviceScreenDecorator())
			add(WindowSizeDecorator(windowSizeInfo))
			add(ConsentsDecorator(application))
			add(ContentUrlDecorator(configurationManager))
			add(StructurePathDecorator(configurationManager))
			add(ReferrerDecorator(configurationManager))
		}

		Logger.debug("Decorators for event: $decorators")
	}

	fun decorate(event: Event): Event
	{
		decorators.forEach { decorator -> decorator.decorate(event) }
		return event
	}
}
