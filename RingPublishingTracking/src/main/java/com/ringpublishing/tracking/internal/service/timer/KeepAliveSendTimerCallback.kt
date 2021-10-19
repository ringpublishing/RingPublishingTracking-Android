/*
 *  Created by Grzegorz Małopolski on 10/13/21, 5:19 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.service.timer

interface KeepAliveSendTimerCallback
{
	fun onSendTimer()
	fun onActivityTimer()
}
