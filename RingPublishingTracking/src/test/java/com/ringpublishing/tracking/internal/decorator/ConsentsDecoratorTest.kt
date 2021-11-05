/*
 *  Created by Grzegorz Małopolski on 10/29/21, 11:42 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.content.Context
import android.content.SharedPreferences
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ConsentsDecoratorTest
{

	@MockK
	lateinit var context: Context

	@MockK
	lateinit var event: Event

	@MockK
	lateinit var sharedPreferences: SharedPreferences

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenConsentStringExist_ThenReturnValue()
	{
		val parameters = mutableMapOf<String, Any>()

		every { context.packageName } returns ""
		every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
		every { sharedPreferences.getString("IABTCF_TCString", any()) } returns "value"
		every { event.parameters } returns parameters

		val consentDecorator = ConsentsDecorator(context)

		consentDecorator.decorate(event)

		Assert.assertEquals("value", event.parameters["_adpc"])
	}

	@Test
	fun decorate_WhenConsentStringNotExist_ThenNoValue()
	{
		val parameters = mutableMapOf<String, Any>()

		every { context.packageName } returns ""
		every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
		every { sharedPreferences.getString("IABTCF_TCString", any()) } returns null
		every { event.parameters } returns parameters

		val consentDecorator = ConsentsDecorator(context)

		consentDecorator.decorate(event)

		Assert.assertNull(event.parameters["_adpc"])
	}
}
