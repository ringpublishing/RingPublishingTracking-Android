/*
 *  Created by Grzegorz Małopolski on 10/14/21, 3:48 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal

import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.config.OperationMode
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.data.UserData
import com.ringpublishing.tracking.internal.decorator.IdGenerator
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.util.PathBuilder
import java.net.URL

internal class ConfigurationManager
{

	internal lateinit var ringPublishingTrackingConfiguration: RingPublishingTrackingConfiguration
		private set

	private val idGenerator = IdGenerator()

	private val pathBuilder = PathBuilder(this)

	private val operationMode = OperationMode()

	private val userData = UserData()

	private var currentAdvertisementArea: String? = null

	var currentReferrer: String? = null

	var currentPublicationUrl: URL? = null

	private var currentStructurePath = mutableListOf<String>()

	var contentPageViewSource = ContentPageViewSource.DEFAULT

	var currentContentUrl: String? = null

	var primaryId: String? = null

	var secondaryId: String? = null

	fun initializeConfiguration(ringPublishingTrackingConfiguration: RingPublishingTrackingConfiguration)
	{
		this.ringPublishingTrackingConfiguration = ringPublishingTrackingConfiguration
		currentAdvertisementArea = ringPublishingTrackingConfiguration.applicationDefaultAdvertisementArea
		currentStructurePath = ringPublishingTrackingConfiguration.applicationDefaultStructurePath.toMutableList()
		currentContentUrl = pathBuilder.buildCurrentContentUrl()
		newPrimaryId()
		Logger.debug("Initialize configuration $ringPublishingTrackingConfiguration")
	}

	fun setDebugMode(enabled: Boolean)
	{
		operationMode.debugEnabled = enabled
		Logger.info("Set debug mode enabled: $enabled")
		Logger.debugLogEnabled(enabled)
	}

	fun setOptOutMode(enabled: Boolean)
	{
		operationMode.optOutEnabled = enabled
		Logger.info("Set Opt out mode enabled $enabled")
	}

	fun isOptOutModeEnabled() = operationMode.optOutEnabled

	fun updateUserData(ssoSystemName: String?, userId: String?)
	{
		userData.ssoName = ssoSystemName
		userData.userId = userId
	}

	fun updateAdvertisementArea(currentArea: String?)
	{
		currentAdvertisementArea = currentArea
	}

	fun getUserData() = userData

	fun getTenantId() = ringPublishingTrackingConfiguration.tenantId

	fun getSiteArea() = currentAdvertisementArea

	fun getStructurePath() = currentStructurePath

	fun getFullStructurePath(): String
	{
		with(ringPublishingTrackingConfiguration)
		{
			val rootPath = if (applicationRootPath.endsWith("/")) applicationRootPath.removeSuffix("/") else applicationRootPath
			return currentStructurePath.joinToString("/", "$rootPath${Constants.defaultRootPathSuffix}/").lowercase().replace(".", "_")
		}
	}

	private fun updateReferrerUrl()
	{
		if (!pathBuilder.isDefaultContentUrl(currentContentUrl))
		{
			currentReferrer = currentContentUrl
		}
	}

	private fun newPrimaryId()
	{
		primaryId = idGenerator.newId()
		secondaryId = primaryId
	}

	private fun newSecondaryId()
	{
		secondaryId = idGenerator.newId()
	}

	fun updatePartiallyReloaded(partiallyReloaded: Boolean)
	{
		if (partiallyReloaded) newSecondaryId() else newPrimaryId()
	}

	fun updateStructurePath(newStructurePath: List<String>, publicationUrl: URL? = null, contentPageViewSource: ContentPageViewSource? = null)
	{
		currentStructurePath.clear()
		currentStructurePath.addAll(newStructurePath)

		if (currentPublicationUrl?.equals(publicationUrl) != true)
		{
			currentPublicationUrl = publicationUrl
		}

		contentPageViewSource?.let { this.contentPageViewSource = it }

		updateReferrerUrl()

		currentContentUrl = pathBuilder.buildCurrentContentUrl()
	}
}
