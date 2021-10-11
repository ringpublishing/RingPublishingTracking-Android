/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.delegate.ConfigurationManager

internal class StructurePathDecorator(private val configurationManager: ConfigurationManager) : BaseDecorator()
{
	private val applicationRootPath get() = configurationManager.ringPublishingTrackingConfiguration.applicationRootPath

	override fun decorate(event: Event)
	{
		event.add(EventParam.PUBLICATION_STRUCTURE_PATH, buildStructurePathDV())
	}

	private fun buildStructurePathDV(): String
	{
		val rootPath = if (applicationRootPath.endsWith("/")) applicationRootPath.removeSuffix("/") else applicationRootPath
		return configurationManager.getStructurePath().joinToString("/", "$rootPath${Constants.defaultRootPathSuffix}/")
	}
}
