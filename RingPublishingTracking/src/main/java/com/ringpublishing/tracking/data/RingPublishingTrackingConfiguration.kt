/*
 *  Created by Grzegorz Małopolski on 9/21/21, 4:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.data

import com.ringpublishing.tracking.internal.constants.Constants
import java.net.URL

/**
 * RingPublishingTracking configuration used for initialization module
 *
 * @property tenantId Tenant identifier
 * @property apiKey API key
 * @property apiUrl Optional API url. If not set, default endpoint will be used.
 * @property applicationRootPath Application root path, for example app name like "onet" or "blick".
 * @property applicationDefaultStructurePath Application default area, for example "home_screen", "undefined" by default
 * @property applicationDefaultAdvertisementArea Default ad space name of the application,
 * For example "ads/list/sport", "undefined" by default
 */
data class RingPublishingTrackingConfiguration(
    val tenantId: String,
    val apiKey: String,
    val apiUrl: URL? = null,
    val applicationRootPath: String,
    val applicationDefaultStructurePath: List<String> = Constants.applicationDefaultStructurePath,
    val applicationDefaultAdvertisementArea: String = Constants.applicationDefaultAdvertisementArea,
)
