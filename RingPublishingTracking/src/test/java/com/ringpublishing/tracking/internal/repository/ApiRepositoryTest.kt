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
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
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
	lateinit var date: Date

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
	fun readIdentify_FromApiRepository_ReadObjectFromDataRepository()
	{
		every { dataRepository.readObject<IdentifyResponse?>(any(), IdentifyResponse::class.java) } returns identifyResponse
		val apiRepository = ApiRepository(dataRepository)

		val result = apiRepository.readIdentify()

		Assert.assertNotNull(result)
	}

	@Test
	fun readIdentifyRequestDate_WhenHaveSavedDate_ThenReturnResult()
	{
		every { dataRepository.readObject<Date?>(any(), Date::class.java) } returns date
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
