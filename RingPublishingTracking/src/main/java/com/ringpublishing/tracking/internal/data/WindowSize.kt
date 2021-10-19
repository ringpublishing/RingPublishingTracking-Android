/*
 *  Created by Grzegorz Małopolski on 10/15/21, 2:53 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.data

import com.ringpublishing.tracking.data.ContentSize

class WindowSize(private val width: Int,private val height: Int)
{
	constructor(contentSize: ContentSize): this(contentSize.width, contentSize.height)

	override fun toString(): String
	{
		return "${width}x$height"
	}
}
