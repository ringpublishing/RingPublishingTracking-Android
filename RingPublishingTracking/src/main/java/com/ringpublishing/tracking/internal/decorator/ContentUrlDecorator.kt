/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.delegate.ConfigurationDelegate

internal class ContentUrlDecorator(private val configurationDelegate: ConfigurationDelegate) : BaseDecorator()
{

	private val applicationRootPath get() = configurationDelegate.ringPublishingTrackingConfiguration.applicationRootPath

	private val publicationUrl get() = configurationDelegate.currentPublicationUrl?.toString()

	private val structurePath get() = configurationDelegate.getStructurePath()

	override fun decorate(event: Event)
	{
		event.add(EventParam.CONTENT_URL, buildContentUrlDU())
	}

	private fun buildContentUrlDU(): String
	{
		return if (!publicationUrl.isNullOrEmpty()) publicationUrl!! else parameterGenerator.buildRootUrl(applicationRootPath, structurePath)
	}
}
