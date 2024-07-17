/*
 *  Created by Grzegorz Małopolski on 10/4/21, 1:00 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.data

data class UserData(
    var userId: String? = null,
    var ssoName: String? = null,
    var emailMd5: String? = null,
    var isActiveSubscriber: Boolean? = null
)
