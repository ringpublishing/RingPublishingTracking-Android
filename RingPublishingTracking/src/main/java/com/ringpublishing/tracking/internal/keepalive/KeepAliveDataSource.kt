/*
 *  Created by Grzegorz Małopolski on 10/18/21, 2:51 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.keepalive

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.KeepAliveContentStatus

internal interface KeepAliveDataSource
{
	fun didAskForKeepAliveContentStatus(contentMetadata: ContentMetadata): KeepAliveContentStatus?
}
