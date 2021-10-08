/*
 *  Created by Grzegorz Małopolski on 10/6/21, 1:44 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.log.Logger

internal open class BaseDecorator : Decorator
{

	protected val parameterGenerator = ParameterGenerator()

	fun Event.add(eventParam: EventParam, value: Any?)
	{
		value?.let { parameters[eventParam.paramName] = it }
	}

	override fun decorate(event: Event) { Logger.warn("Missing decoration in class ${this.javaClass.canonicalName}") }
}
