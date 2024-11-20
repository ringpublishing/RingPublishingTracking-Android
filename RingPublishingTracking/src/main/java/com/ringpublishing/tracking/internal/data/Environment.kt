/*
 *  Created by Grzegorz Małopolski on 4/12/22, 9:56 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.data

import com.google.gson.annotations.SerializedName

/**
 * Enum class representing environment of the client
 * @property Production production environment
 * @property Integration integration environment
 * @property Development development environment
 */
enum class Environment
{
    @SerializedName("prod")
    Production,
    @SerializedName("int")
    Integration,
    @SerializedName("dev")
    Development
}

