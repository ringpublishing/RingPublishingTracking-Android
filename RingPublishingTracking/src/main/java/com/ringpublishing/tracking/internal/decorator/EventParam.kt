/*
 *  Created by Grzegorz Małopolski on 10/1/21, 2:25 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

enum class EventParam(val paramName: String)
{
	PRIMARY_ID("IP"),
	SECONDARY_ID("IV"),
	USER_SSO_DATA("RDLU"),
	TENANT_ID("TID"),
	SITE_AREA("DA"),
	CONTENT_URL("DU"),
	CONTENT_REFERER("DR"),
	PUBLICATION_STRUCTURE_PATH("DV"),
	SCREEN_SIZE("CS"),
	WINDOW_SIZE("CV"),
	CONSENT("_adpc")
}
