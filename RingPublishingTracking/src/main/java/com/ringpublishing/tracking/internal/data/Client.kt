/*
 *  Created by Grzegorz Małopolski on 4/12/22, 9:55 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.data

import android.util.Base64
import com.google.gson.Gson

class Client @JvmOverloads constructor(val client: ClientType, val variant: ClientVariant? = null)

internal fun Client.toRdlc(gson: Gson): String = Base64.encodeToString(
	gson.toJson(this).toByteArray(Charsets.UTF_8),
	Base64.NO_WRAP,
)
