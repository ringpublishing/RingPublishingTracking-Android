/*
 *  Created by Grzegorz Małopolski on 10/1/21, 1:11 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.content.Context
import com.google.gson.Gson
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.ConfigurationManager
import com.ringpublishing.tracking.internal.log.Logger

internal class EventDecorator(configurationDelegate: ConfigurationManager, context: Context, gson: Gson)
{
	private val decorators = mutableListOf<Decorator>()

	init
	{
		with(decorators)
		{
			add(PrimaryIdDecorator(configurationDelegate))
			add(SecondaryIdDecorator(configurationDelegate))
			add(UserSsoDataDecorator(configurationDelegate, gson))
			add(TenantIdDecorator(configurationDelegate))
			add(SiteAreaDecorator(configurationDelegate))
			add(DeviceScreenDecorator(context))
			add(WindowSizeDecorator(context))
			add(ConsentsDecorator(context))
			add(ContentUrlDecorator(configurationDelegate))
			add(StructurePathDecorator(configurationDelegate))
			add(ReferrerDecorator(configurationDelegate))
		}

		Logger.debug("Decorators for event: $decorators")
	}

	fun decorate(event: Event): Event
	{
		decorators.forEach { decorator -> decorator.decorate(event) }
		return event
	}
}
