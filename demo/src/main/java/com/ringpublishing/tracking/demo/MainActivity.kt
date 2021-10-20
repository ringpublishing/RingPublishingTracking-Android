package com.ringpublishing.tracking.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ringpublishing.tracking.demo.adapter.MainPagerAdapter
import com.ringpublishing.tracking.demo.fragment.ActionsFragment
import com.ringpublishing.tracking.demo.fragment.ListArticlesFragment
import com.ringpublishing.tracking.demo.fragment.NoContentFragment

class MainActivity : AppCompatActivity()
{

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val tabViewpager = findViewById<ViewPager>(R.id.tab_viewpager)
		val tabTabLayout = findViewById<TabLayout>(R.id.tab_tab_layout)

		tabViewpager.adapter = createAdapterWithFragments()
		tabTabLayout.setupWithViewPager(tabViewpager)
	}

	private fun createAdapterWithFragments(): MainPagerAdapter
	{
		return MainPagerAdapter(supportFragmentManager).apply {
			addFragment("ARTICLES", ListArticlesFragment())
			addFragment("ACTIONS", ActionsFragment())
			addFragment("WEBVIEW", NoContentFragment())
		}
	}
}
