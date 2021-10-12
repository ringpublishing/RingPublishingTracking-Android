/*
 *  Created by Grzegorz Małopolski on 10/1/21, 2:25 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

internal enum class EventParam(val text: String)
{
	PRIMARY_ID("ip"),
	SECONDARY_ID("iv"),
	USER_SSO_DATA("rdlu"),
	TENANT_ID("tid"),
	SITE_AREA("da"),
	CONTENT_URL("du"),
	CONTENT_REFERER("dr"),
	PUBLICATION_STRUCTURE_PATH("dv"),
	SCREEN_SIZE("cs"),
	WINDOW_SIZE("cv"),
	CONSENT("_adpc"),
}
