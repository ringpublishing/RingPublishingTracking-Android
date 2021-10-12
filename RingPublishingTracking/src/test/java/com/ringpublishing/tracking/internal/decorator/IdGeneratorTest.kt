/*
 *  Created by Grzegorz Małopolski on 10/7/21, 12:31 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class IdGeneratorTest
{

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenGenerateIsCalled_ThenGenerateNewValue()
	{
		val idGenerator = IdGenerator()

		repeat(9999)
		{
			val id = idGenerator.newId()
			Assert.assertTrue(id.length > 7)
		}
	}
}
