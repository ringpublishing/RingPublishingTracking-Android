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
import com.ringpublishing.tracking.internal.service.builder.md5
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

	fun isSendEventsBlocked() = operationMode.optOutEnabled || operationMode.debugEnabled

    fun updateSsoSystemName(ssoSystemName: String?) {
        userData.ssoName = ssoSystemName
    }

	fun updateUserData(userId: String?, userEmail: String?, isActiveSubscriber: Boolean?)
	{
		userData.userId = userId
		userData.isActiveSubscriber = isActiveSubscriber
		userEmail?.let { userData.emailMd5 = it.trim().lowercase().replace("[\n\r]", "").md5() }
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
			return currentStructurePath.joinToString("/", "$rootPath${Constants.defaultRootPathSuffixDV}/").lowercase().replace(".", "_")
		}
	}

	private fun updateReferrerUrl()
	{
		if (!pathBuilder.isDefaultContentUrl(currentContentUrl))
		{
			currentReferrer = currentContentUrl
		}
	}

	fun getRootPath() = ringPublishingTrackingConfiguration.applicationRootPath

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

	fun updateStructurePath(newStructurePath: List<String>, publicationUrl: URL? = null, contentPageViewSource: ContentPageViewSource? = null, partiallyReloaded: Boolean)
	{
		currentStructurePath.clear()
		currentStructurePath.addAll(newStructurePath)

		if (currentPublicationUrl?.equals(publicationUrl) != true)
		{
			currentPublicationUrl = publicationUrl
		}

		contentPageViewSource?.let { this.contentPageViewSource = it }

		if (!partiallyReloaded)
		{
			updateReferrerUrl()
		}

		currentContentUrl = pathBuilder.buildCurrentContentUrl()
	}
}
