package com.ringpublishing.tracking.internal.delegate

import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.config.OperationMode
import com.ringpublishing.tracking.internal.data.UserData
import com.ringpublishing.tracking.internal.log.Logger
import java.net.URL

internal class ConfigurationManager
{

	internal lateinit var ringPublishingTrackingConfiguration: RingPublishingTrackingConfiguration

	private val operationMode = OperationMode()

	private val userData = UserData()

	private var currentAdvertisementArea: String? = null

	var currentReferrer: URL? = null

	var currentPublicationUrl: URL? = null

	private var currentStructurePath = mutableListOf<String>()

	var currentIsPartialView = false

	var primaryId: String? = null

	var secondaryId: String? = null

	fun initializeConfiguration(ringPublishingTrackingConfiguration: RingPublishingTrackingConfiguration)
	{
		this.ringPublishingTrackingConfiguration = ringPublishingTrackingConfiguration
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

	fun updateCurrentStructurePath(structurePath: List<String>)
	{
		currentStructurePath.clear()
		currentStructurePath.addAll(structurePath)
	}

	fun getUserData() = userData

	fun getTenantId() = ringPublishingTrackingConfiguration.tenantId

	fun getSiteArea() = if (currentAdvertisementArea.isNullOrEmpty()) ringPublishingTrackingConfiguration.applicationDefaultAdvertisementArea else currentAdvertisementArea

	fun getStructurePath() = if (currentStructurePath.isEmpty()) ringPublishingTrackingConfiguration.applicationDefaultStructurePath else currentStructurePath

	fun updateCurrentPublicationUrl(publicationUrl: URL)
	{
		updateReferrer()
		currentPublicationUrl = publicationUrl
	}

	private fun updateReferrer()
	{
		currentReferrer = currentPublicationUrl
	}

	fun newPrimaryId()
	{
		primaryId = null
		secondaryId = null
	}

	fun newSecondaryId()
	{
		secondaryId = null
	}
}
