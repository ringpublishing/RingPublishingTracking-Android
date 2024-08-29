/*
 *  Created by Grzegorz Małopolski on 10/28/21, 5:12 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal

import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ConfigurationManagerTest
{

	@MockK
	lateinit var ringPublishingConfiguration: RingPublishingTrackingConfiguration

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun initializeConfiguration_WhenSetConfiguration_ThenFieldsAreSet()
	{
		val configurationManager = ConfigurationManager()

		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf()
		every { ringPublishingConfiguration.applicationRootPath } returns ""

		configurationManager.initializeConfiguration(ringPublishingConfiguration)

		verify { ringPublishingConfiguration.applicationDefaultAdvertisementArea }
		verify { ringPublishingConfiguration.applicationDefaultStructurePath }
		verify { ringPublishingConfiguration.applicationRootPath }
		Assert.assertNotNull(configurationManager.primaryId)
	}

	@Test
	fun setDebugMode_WhenDebugEnabled_ThenSendEventsBlocked()
	{
		val configurationManager = ConfigurationManager()
		configurationManager.setDebugMode(true)
		Assert.assertTrue(configurationManager.isSendEventsBlocked())
	}

	@Test
	fun setDebugMode_WhenDebugDisabled_ThenSendEventsBlocked()
	{
		val configurationManager = ConfigurationManager()
		configurationManager.setDebugMode(false)
		Assert.assertFalse(configurationManager.isSendEventsBlocked())
	}

	@Test
	fun setOptOutMode_WhenOptOutEnabled_ThenSendEventsBlocked()
	{
		val configurationManager = ConfigurationManager()
		configurationManager.setOptOutMode(true)
		Assert.assertTrue(configurationManager.isSendEventsBlocked())
	}

	@Test
	fun isSendEventsBlocked_WhenDebugAndOptOutMode_ThenSendEventsBlocked()
	{
		val configurationManager = ConfigurationManager()
		configurationManager.setDebugMode(true)
		configurationManager.setOptOutMode(true)
		Assert.assertTrue(configurationManager.isSendEventsBlocked())
	}

	@Test
	fun updateUserData_WhenSet_ThenHaveAllValues()
	{
		val configurationManager = ConfigurationManager()

		configurationManager.updateSsoSystemName("sso")
        configurationManager.updateIsActiveSubscriber(true)
		configurationManager.updateUserData("userId", "email")

		val userData = configurationManager.getUserData()

		Assert.assertNotNull(userData)
		Assert.assertEquals("sso", userData.ssoName)
		Assert.assertEquals("userId", userData.userId)
		Assert.assertTrue(userData.emailMd5?.isNotEmpty() == true)
		Assert.assertTrue(userData.isActiveSubscriber == true)
	}

	@Test
	fun updateAdvertisementArea_WhenSet_ThenHaveValue()
	{
		val configurationManager = ConfigurationManager()
		configurationManager.updateAdvertisementArea("area")
		Assert.assertEquals("area", configurationManager.getSiteArea())
	}

	@Test
	fun getUserData_WhenDataAndSetTONull_ThenNoUserData()
	{
		val configurationManager = ConfigurationManager()

        configurationManager.updateSsoSystemName("sso")
        configurationManager.updateIsActiveSubscriber(true)
        configurationManager.updateUserData("userId", "email")

        configurationManager.updateSsoSystemName(null)
        configurationManager.updateIsActiveSubscriber(null)
        configurationManager.updateUserData(null, null)

		Assert.assertNull(configurationManager.getUserData().userId)
		Assert.assertNull(configurationManager.getUserData().ssoName)
		Assert.assertNull(configurationManager.getUserData().isActiveSubscriber)
	}

	@Test
	fun getTenantId_WhenSetInConfiguration_ThanHaveValue()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf()
		every { ringPublishingConfiguration.applicationRootPath } returns ""
		every { ringPublishingConfiguration.tenantId } returns "tenantId"

		val configurationManager = ConfigurationManager()

		configurationManager.initializeConfiguration(ringPublishingConfiguration)

		Assert.assertEquals("tenantId", configurationManager.getTenantId())
	}

	@Test
	fun getSiteArea()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns "area"
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf()
		every { ringPublishingConfiguration.applicationRootPath } returns ""

		val configurationManager = ConfigurationManager()
		configurationManager.initializeConfiguration(ringPublishingConfiguration)

		Assert.assertEquals("area", configurationManager.getSiteArea())
	}

	@Test
	fun getStructurePath_WhenPathIsEmpty_ThenResultIsEmpty()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf()
		every { ringPublishingConfiguration.applicationRootPath } returns ""

		val configurationManager = ConfigurationManager()
		configurationManager.initializeConfiguration(ringPublishingConfiguration)

		Assert.assertEquals(mutableListOf<String>(), configurationManager.getStructurePath())
		Assert.assertTrue(configurationManager.getStructurePath().isEmpty())
	}

	@Test
	fun getStructurePath_WhenPathHaveTwoValues_ThenResultIsSame()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf("path1", "path2")
		every { ringPublishingConfiguration.applicationRootPath } returns ""

		val configurationManager = ConfigurationManager()
		configurationManager.initializeConfiguration(ringPublishingConfiguration)

		Assert.assertEquals(listOf("path1", "path2"), configurationManager.getStructurePath())
	}

	@Test
	fun getFullStructurePath_WhenRootPathSet_ThenHaveResult()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf("path1", "path2")
		every { ringPublishingConfiguration.applicationRootPath } returns "rootPath"

		val configurationManager = ConfigurationManager()
		configurationManager.initializeConfiguration(ringPublishingConfiguration)

		Assert.assertEquals("rootpath_app_android/path1/path2", configurationManager.getFullStructurePath())
	}

	@Test
	fun getFullStructurePath_WhenPathHaveDots_Then_ResultHaveHardSpaces()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf("pa.th1", "pa.th2")
		every { ringPublishingConfiguration.applicationRootPath } returns "rootPath"

		val configurationManager = ConfigurationManager()
		configurationManager.initializeConfiguration(ringPublishingConfiguration)

		Assert.assertEquals("rootpath_app_android/pa_th1/pa_th2", configurationManager.getFullStructurePath())
	}

	@Test
	fun getFullStructurePath_WhenRootPathHaveBigLetters_ThenResultHaveSmallLetters()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf("PAth1", "PAth2")
		every { ringPublishingConfiguration.applicationRootPath } returns "RootPath"

		val configurationManager = ConfigurationManager()
		configurationManager.initializeConfiguration(ringPublishingConfiguration)

		Assert.assertEquals("rootpath_app_android/path1/path2", configurationManager.getFullStructurePath())
	}

	@Test
	fun getFullStructurePath_WhenRootPathHaveEndChar_ThenResultIsCorrect()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf("path1", "path2")
		every { ringPublishingConfiguration.applicationRootPath } returns "rootpath/"

		val configurationManager = ConfigurationManager()
		configurationManager.initializeConfiguration(ringPublishingConfiguration)

		Assert.assertEquals("rootpath_app_android/path1/path2", configurationManager.getFullStructurePath())
	}

	@Test
	fun updatePartiallyReloaded_WhenTrue_ThenSecondIdIsDifferent()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf()
		every { ringPublishingConfiguration.applicationRootPath } returns ""

		val configurationManager = ConfigurationManager()
		configurationManager.initializeConfiguration(ringPublishingConfiguration)
		val primaryId = configurationManager.primaryId
		val secondaryId = configurationManager.secondaryId

		configurationManager.updatePartiallyReloaded(true)

		Assert.assertEquals(primaryId, configurationManager.primaryId)
		Assert.assertNotEquals(secondaryId, configurationManager.secondaryId)
	}

	@Test
	fun updatePartiallyReloaded_WhenFalse_ThenIdsAreDifferent()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf()
		every { ringPublishingConfiguration.applicationRootPath } returns ""

		val configurationManager = ConfigurationManager()
		configurationManager.initializeConfiguration(ringPublishingConfiguration)
		val primaryId = configurationManager.primaryId
		val secondaryId = configurationManager.secondaryId

		configurationManager.updatePartiallyReloaded(false)

		Assert.assertNotEquals(primaryId, configurationManager.primaryId)
		Assert.assertNotEquals(secondaryId, configurationManager.secondaryId)
	}

	@Test
	fun updateStructurePath_WhenUpdated_ThanHaveNewValue()
	{
		every { ringPublishingConfiguration.applicationDefaultAdvertisementArea } returns ""
		every { ringPublishingConfiguration.applicationDefaultStructurePath } returns listOf("path1")
		every { ringPublishingConfiguration.applicationRootPath } returns ""

		val configurationManager = ConfigurationManager()
		configurationManager.initializeConfiguration(ringPublishingConfiguration)

		configurationManager.updateStructurePath(listOf("newPath"), partiallyReloaded = false)

		Assert.assertEquals(listOf("newPath"), configurationManager.getStructurePath())
	}
}
