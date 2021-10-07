/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.ConfigurationManager

internal class ContentUrlDecorator(private val configurationManager: ConfigurationManager) : BaseDecorator()
{

	private val applicationRootPath get() = configurationManager.ringPublishingTrackingConfiguration.applicationRootPath

	private val structurePath get() = configurationManager.getStructurePath()

	override fun decorate(event: Event)
	{
		event.add(EventParam.CONTENT_URL, buildContentUrlDU())
	}

	private fun buildContentUrlDU(): String
	{
		val url = configurationManager.currentPublicationUrl?.toString()
		return if (!url.isNullOrEmpty()) url else parameterGenerator.buildRootUrl(applicationRootPath, structurePath)
	}
}
