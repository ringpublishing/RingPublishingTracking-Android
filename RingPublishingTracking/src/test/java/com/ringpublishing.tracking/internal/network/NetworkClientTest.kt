package com.ringpublishing.tracking.internal.network

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
