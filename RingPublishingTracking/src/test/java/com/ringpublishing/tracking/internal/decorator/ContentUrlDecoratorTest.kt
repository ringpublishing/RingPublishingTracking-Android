/*
 *  Created by Grzegorz Małopolski on 10/7/21, 1:09 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.data.Environment
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL

class ContentUrlDecoratorTest
{

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenNormalConfiguration_ThenCorrectContentUrl()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"rootPath",
			listOf("path1", "path2"),
			"area",
            Environment.Development
		)

		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)

		val contentUrlDecorator = ContentUrlDecorator(configurationManager)

		val event = Event()
		contentUrlDecorator.decorate(event)

		val result = event.parameters[EventParam.CONTENT_URL.text] as String?

		Assert.assertNotNull(result)
		Assert.assertEquals("https://rootpath.app.android/path1/path2", result)
	}

	@Test
	fun decorate_WhenLongerPath_ThenCorrectContentUrl()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"rootPath",
			listOf("path1", "path2", "path3", "path4", "path5"),
			"area",
            Environment.Development
		)

		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)

		val contentUrlDecorator = ContentUrlDecorator(configurationManager)

		val event = Event()
		contentUrlDecorator.decorate(event)

		val result = event.parameters[EventParam.CONTENT_URL.text] as String?

		Assert.assertNotNull(result)
		Assert.assertEquals("https://rootpath.app.android/path1/path2/path3/path4/path5", result)
	}

	@Test
	fun decorate_WhenRootPathWithEndChar_ThenCorrectContentUrl()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"rootPath/",
			listOf("path1", "path2"),
			"area",
            Environment.Development
		)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)
		val contentUrlDecorator = ContentUrlDecorator(configurationManager)

		val event = Event()
		contentUrlDecorator.decorate(event)

		val result = event.parameters[EventParam.CONTENT_URL.text] as String?

		Assert.assertNotNull(result)
		Assert.assertEquals("https://rootpath.app.android/path1/path2", result)
	}

	@Test
	fun decorate_WhenPublicationUrlIsSet_ThenPublicationUriIsInResult()
	{
		val configurationManager = ConfigurationManager()
		val ringPublishingTrackingConfiguration = RingPublishingTrackingConfiguration(
			"",
			"",
			URL("https://domain.com"),
			"rootPath",
			listOf("path1", "path2"),
			"area",
            Environment.Development
		)
		configurationManager.initializeConfiguration(ringPublishingTrackingConfiguration)

		configurationManager.updateStructurePath(listOf("path1", "path2"), URL("https://publicationurl.com"), partiallyReloaded = false)

		val contentUrlDecorator = ContentUrlDecorator(configurationManager)

		val event = Event()
		contentUrlDecorator.decorate(event)

		val result = event.parameters[EventParam.CONTENT_URL.text] as String?

		Assert.assertNotNull(result)
		Assert.assertEquals("https://publicationurl.com", result)
	}
}
