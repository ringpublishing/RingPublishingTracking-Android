/*
 *  Created by Marcin Nowacki on 9/7/26, 3:00 PM
 * Copyright © 2026 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event

internal class SequenceDecorator(private var sequence: Int = 0) : BaseDecorator()
{

	override fun decorate(event: Event)
	{
		event.add(EventParam.SEQUENCE, sequence)
	}

	override fun eventDecorated()
	{
		sequence = nextValue(sequence)
	}

	private fun nextValue(value: Int) = if (value == Int.MAX_VALUE) 0 else value + 1
}
