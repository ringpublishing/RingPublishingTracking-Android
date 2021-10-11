/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:29 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event

internal interface Decorator
{

	fun decorate(event: Event)
}
