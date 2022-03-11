/*
 *  Created by Grzegorz Małopolski on 10/28/21, 3:46 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.repository

import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.DataRepository
import com.ringpublishing.tracking.internal.util.isIdentifyExpire
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Date

class ApiRepositoryTest
{

	@MockK
	internal lateinit var dataRepository: DataRepository

	@MockK
	internal lateinit var identifyResponse: IdentifyResponse

	@MockK
	internal lateinit var date: Date

	@MockK
	internal lateinit var expirationDate: Date

	@MockK
	internal lateinit var identifyDate: Date

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun saveIdentify_WhenSaveObject_ThenSaveToRepository()
	{
		val apiRepository = ApiRepository(dataRepository)

		apiRepository.saveIdentify(identifyResponse)

		verify { dataRepository.saveObject("identify", identifyResponse) }
	}

	@Test
	fun readIdentify_FromApiRepository_ReadObjectFromDataRepository_WhenIdentifierIsUpToDate()
	{
		prepareMocksForReadingIdentify(false)
		every { identifyResponse?.getValidDate(any())} returns expirationDate

		val apiRepository = ApiRepository(dataRepository)

		val result = apiRepository.readIdentify()

		Assert.assertNotNull(result)
	}

	@Test
	fun readNull_FromApiRepository_ReadObjectFromDataRepository_WhenIdentifierIsExpired()
	{
		prepareMocksForReadingIdentify(true)

		val apiRepository = ApiRepository(dataRepository)

		val result = apiRepository.readIdentify()

		Assert.assertNull(result)
	}

	private fun prepareMocksForReadingIdentify(isExpired: Boolean)
	{
		mockkStatic("com.ringpublishing.tracking.internal.util.Date_IndetifierExpireKt")
		every { dataRepository.readObject<IdentifyResponse?>(any(), IdentifyResponse::class.java) } returns identifyResponse
		every { dataRepository.readLong(any()) } returns 0L

		every { identifyResponse.getValidDate(identifyDate) } returns expirationDate
		every { identifyResponse.getValidDate(null) } returns null
		every { expirationDate.isIdentifyExpire() } returns isExpired
	}

	@Test
	fun readIdentifyRequestDate_WhenHaveSavedDate_ThenReturnResult()
	{
		every { dataRepository.readLong(any()) } returns 123240L
		val apiRepository = ApiRepository(dataRepository)

		val result = apiRepository.readIdentifyRequestDate()

		Assert.assertNotNull(result)
	}

	@Test
	fun removeIdentify_WhenCalled_ThenRepositoryRemoveObject()
	{
		val apiRepository = ApiRepository(dataRepository)

		apiRepository.removeIdentify()

		verify(exactly = 2) { dataRepository.remove(any()) }
	}
}
