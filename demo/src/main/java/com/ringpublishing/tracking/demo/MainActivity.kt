package com.ringpublishing.tracking.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.ringpublishing.tracking.demo.adapter.MainPagerAdapter
import com.ringpublishing.tracking.demo.fragment.ActionsFragment
import com.ringpublishing.tracking.demo.fragment.AudioActionsFragment
import com.ringpublishing.tracking.demo.fragment.ListArticlesFragment
import com.ringpublishing.tracking.demo.fragment.NoContentFragment
import com.ringpublishing.tracking.demo.fragment.PaidFragment
import com.ringpublishing.tracking.demo.fragment.VideoActionsFragment

class MainActivity : AppCompatActivity()
{

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
        initTopInsets()

		val tabViewpager = findViewById<ViewPager>(R.id.tab_viewpager)
		val tabTabLayout = findViewById<TabLayout>(R.id.tab_tab_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

		tabViewpager.adapter = createAdapterWithFragments()
		tabTabLayout.setupWithViewPager(tabViewpager)
	}

	private fun createAdapterWithFragments(): MainPagerAdapter
	{
		return MainPagerAdapter(supportFragmentManager).apply {
			addFragment("ARTICLES", ListArticlesFragment())
			addFragment("ACTIONS", ActionsFragment())
            addFragment("VIDEO ACTIONS", VideoActionsFragment())
            addFragment("AUDIO ACTIONS", AudioActionsFragment())
            addFragment("WEBVIEW", NoContentFragment())
            addFragment("PAID", PaidFragment())
		}
	}

    private fun initTopInsets() {
        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar_layout)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { v: View, insets: WindowInsetsCompat ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            v.setPadding(0, statusBarHeight, 0, 0)
            WindowInsetsCompat.Builder(insets).build()
        }
    }
}
