/*
 *  Created by Grzegorz Małopolski on 10/28/21, 3:46 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.network

import com.ringpublishing.tracking.internal.network.NetworkClient
import com.ringpublishing.tracking.internal.network.UserAgentInterceptorInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class NetworkClientTest
{

	@MockK
	internal lateinit var userAgentInterceptorInfo: UserAgentInterceptorInfo

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
	}

	@Test
	fun get_Client_AddHeader()
	{
		every { userAgentInterceptorInfo.getHeader() } returns "header"

		val networkClient = NetworkClient(userAgentInterceptorInfo)

		val client = networkClient.get()

		Assert.assertTrue(client.networkInterceptors.size == 1)
		verify { userAgentInterceptorInfo.getHeader() }
	}
}
