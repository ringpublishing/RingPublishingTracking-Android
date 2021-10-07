/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.constants.Constants

internal class ConsentsDecorator(context: Context) : BaseDecorator()
{

	private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

	private var consentString: String? = null

	private val preferencesListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
		if (key.equals(Constants.consentStringPreferenceName))
		{
			consentString = preferences.getString(Constants.consentStringPreferenceName, null)
		}
	}

	init
	{
		sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesListener)
	}

	override fun decorate(event: Event)
	{
		event.add(EventParam.CONSENT, getConsentString())
	}

	private fun getConsentString(): String?
	{
		if (consentString.isNullOrEmpty())
		{
			consentString = sharedPreferences.getString(Constants.consentStringPreferenceName, null)
		}

		return consentString
	}
}
