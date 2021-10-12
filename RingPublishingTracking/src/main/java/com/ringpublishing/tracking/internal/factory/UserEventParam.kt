/*
 *  Created by Grzegorz Małopolski on 10/11/21, 4:27 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

internal enum class UserEventParam(val text: String)
{
	SELECTED_ELEMENT_NAME("VE"),
	TARGET_URL("VU"),
	USER_ACTION_CATEGORY_NAME("VE"),
	USER_ACTION_SUBTYPE_NAME("VC"),
	USER_ACTION_PAYLOAD("VM"),
	PAGE_VIEW_CONTENT_INFO("DX"),
	PAGE_VIEW_RESOURCE_IDENTIFIER("PU"),
}
