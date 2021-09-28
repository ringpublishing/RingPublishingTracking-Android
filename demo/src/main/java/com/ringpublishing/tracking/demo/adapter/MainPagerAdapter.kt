/*
 *  Created by Grzegorz Małopolski on 9/22/21, 1:04 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.adapter

import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainPagerAdapter(supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager)
{

	private val data = mutableListOf<Pair<String, Fragment>>()

	@Nullable
	override fun getItem(position: Int) = data[position].second

	@Nullable
	override fun getPageTitle(position: Int) = data[position].first

	override fun getCount() = data.size

	fun addFragment(title: String, fragment: Fragment) {
		data.add(Pair(title, fragment))
	}
}
