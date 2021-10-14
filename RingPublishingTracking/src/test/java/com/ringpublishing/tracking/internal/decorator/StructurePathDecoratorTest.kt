/*
 *  Created by Grzegorz Małopolski on 10/7/21, 2:57 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.ConfigurationManager
import org.junit.Assert
import org.junit.Test
import java.net.URL

class StructurePathDecoratorTest
{

	@Test
	fun decorate_WhenCurrentStructurePathUpdated_ThenUseUpdatedValue()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"rootPath",
			listOf("path1", "path2"),
			"area"
		)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)
		configurationManager.updateStructurePath(listOf("path3", "path4"))

		val decorator = StructurePathDecorator(configurationManager)

		val event = Event()

		decorator.decorate(event)

		val result = event.parameters[EventParam.PUBLICATION_STRUCTURE_PATH.text] as String?

		Assert.assertEquals("rootpath.app.android/path3/path4", result)
	}

	@Test
	fun decorate_WhenCurrentStructurePathDefault_ThenUseDefaultValue()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"rootPath",
			listOf("path1", "path2"),
			"area"
		)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)

		val decorator = StructurePathDecorator(configurationManager)

		val event = Event()

		decorator.decorate(event)

		val result = event.parameters[EventParam.PUBLICATION_STRUCTURE_PATH.text] as String?

		Assert.assertEquals("rootpath.app.android/path1/path2", result)
	}

	@Test
	fun decorate_WhenRootPathEndsWithSlash_ThenResultIsCorrect()
	{
		val configurationManager = ConfigurationManager()

		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"rootPath/",
			listOf("path1", "path2"),
			"area"
		)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)
		val decorator = StructurePathDecorator(configurationManager)

		val event = Event()

		decorator.decorate(event)

		val result = event.parameters[EventParam.PUBLICATION_STRUCTURE_PATH.text] as String?

		Assert.assertEquals("rootpath.app.android/path1/path2", result)
	}
}
