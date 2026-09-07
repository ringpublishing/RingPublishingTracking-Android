/*
 *  Created by Grzegorz Małopolski on 10/1/21, 1:11 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.google.gson.Gson
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.device.WindowSizeInfo
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.util.ScreenSizeInfo

internal class EventDecorator(
    configurationManager: ConfigurationManager,
    apiRepository: ApiRepository,
    gson: Gson,
    windowSizeInfo: WindowSizeInfo,
    screenSizeInfo: ScreenSizeInfo,
)
{
	private val decorators = mutableListOf<Decorator>()

	private val clientDecorator = ClientDecorator(gson)

	init
	{
		with(decorators)
		{
			add(PrimaryIdDecorator(configurationManager))
			add(SecondaryIdDecorator(configurationManager))
			add(SessionIdentifierDecorator())
			add(UserIdentifierDataDecorator(configurationManager, apiRepository, gson))
			add(TenantIdDecorator(configurationManager))
			add(SiteAreaDecorator(configurationManager))
			add(DeviceScreenDecorator(screenSizeInfo))
			add(WindowSizeDecorator(windowSizeInfo, screenSizeInfo))
			add(ContentUrlDecorator(configurationManager))
			add(StructurePathDecorator(configurationManager))
			add(ReferrerDecorator(configurationManager))
			add(clientDecorator)
			add(SequenceDecorator())
		}

		Logger.debug("Decorators for event: $decorators")
	}

	fun decorate(event: Event): Event
	{
		decorators.forEach { decorator ->
			decorator.decorate(event)
			decorator.eventDecorated()
		}
		return event
	}

	fun updateVariantExternalParameters(parameters: Map<String, String>)
	{
		clientDecorator.updateVariantExternalParameters(parameters)
	}
}
