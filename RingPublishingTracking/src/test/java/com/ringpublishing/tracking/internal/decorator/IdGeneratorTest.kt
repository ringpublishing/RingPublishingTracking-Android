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
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class IdGeneratorTest
{

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun decorate_WhenGenerateIsCalled_ThenGenerateNewValueWithCorrectLength()
	{
		val idGenerator = IdGenerator()

		repeat(9999)
		{
			val id = idGenerator.newId()
			Assert.assertTrue(id.length == 24)
			Assert.assertTrue(id.all { it in '0'..'9' })
		}
	}

	@Test
	fun newId_WhenAfternoon_ThenTimestampUsesTwentyFourHourClock()
	{
		val afternoon = GregorianCalendar(2026, Calendar.NOVEMBER, 10, 14, 5, 9).apply { set(Calendar.MILLISECOND, 500) }
		val idGenerator = IdGenerator { afternoon }

		val id = idGenerator.newId()

		Assert.assertEquals("20261110140509500", id.substring(0, 17))
	}

	@Test
	fun newId_WhenDefaultLocaleUsesNonGregorianCalendar_ThenTimestampUsesGregorianYear()
	{
		val defaultLocale = Locale.getDefault()
		try
		{
			listOf(Locale.forLanguageTag("th-TH"), Locale.forLanguageTag("ja-JP-u-ca-japanese")).forEach { locale ->
				Locale.setDefault(locale)

				val id = IdGenerator().newId()

				Assert.assertEquals("Year for $locale", GregorianCalendar().get(Calendar.YEAR).toString(), id.substring(0, 4))
			}
		}
		finally
		{
			Locale.setDefault(defaultLocale)
		}
	}
}
