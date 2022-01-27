package com.ringpublishing.tracking.internal.util

import java.util.Date

internal fun Date?.isIdentifyExpire(): Boolean
{
	return this == null || this.before(Date())
}