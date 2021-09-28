package com.ringpublishing.tracking.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.ringpublishing.tracking.demo.adapter.MainPagerAdapter
import com.ringpublishing.tracking.demo.fragment.ActionsFragment
import com.ringpublishing.tracking.demo.fragment.ListArticlesFragment
import com.ringpublishing.tracking.demo.fragment.NoContentFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity()
{

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val tabViewpager = findViewById<ViewPager>(R.id.tab_viewpager)
		val tabTabLayout = findViewById<TabLayout>(R.id.tab_tablayout)

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
