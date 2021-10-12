/*
 *  Created by Grzegorz Małopolski on 10/11/21, 4:27 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.factory

internal enum class UserEventParam(val text: String)
{
	SELECTED_ELEMENT_NAME("ve"),
	TARGET_URL("vu"),
	USER_ACTION_CATEGORY_NAME("ve"),
	USER_ACTION_SUBTYPE_NAME("vc"),
	USER_ACTION_PAYLOAD("vm"),
	PAGE_VIEW_CONTENT_INFO("dx"),
	PAGE_VIEW_RESOURCE_IDENTIFIER("pu"),
}
