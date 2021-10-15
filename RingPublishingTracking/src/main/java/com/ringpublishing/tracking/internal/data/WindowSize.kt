/*
 *  Created by Grzegorz Małopolski on 10/15/21, 2:53 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.data

class WindowSize(val width: Int, val height: Int)
{
	override fun toString(): String
	{
		return "${width}x$height"
	}
}
