/*
 *  Created by Grzegorz Małopolski on 10/6/21, 1:26 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import java.util.Calendar
import java.util.Random

internal class IdGenerator
{

	fun newId(): String
	{
		with(Calendar.getInstance())
		{
			var iv = get(Calendar.YEAR) % 100L + 2000L
			iv = iv * 100 + get(Calendar.MONTH) + 1
			iv = iv * 100 + get(Calendar.DATE)
			iv = iv * 100 + get(Calendar.HOUR)
			iv = iv * 100 + get(Calendar.MINUTE)
			iv = iv * 100 + get(Calendar.SECOND)
			iv = iv * 1000 + get(Calendar.MILLISECOND)
			iv += ("" + ((1 + Random().nextInt()) * 10000000L)).substring(0, 8).toLong()
			return iv.toString()
		}
	}
}
