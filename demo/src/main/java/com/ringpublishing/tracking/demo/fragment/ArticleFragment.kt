/*
 *  Created by Grzegorz Małopolski on 9/22/21, 1:03 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentSize
import com.ringpublishing.tracking.data.KeepAliveContentStatus
import com.ringpublishing.tracking.delegate.RingPublishingTrackingKeepAliveDataSource
import com.ringpublishing.tracking.demo.R
import com.ringpublishing.tracking.demo.controller.ArticleController
import com.ringpublishing.tracking.demo.extension.isFromPush
import com.ringpublishing.tracking.demo.extension.readArticlePosition

class ArticleFragment : Fragment(R.layout.fragment_article), RingPublishingTrackingKeepAliveDataSource
{

	private val articleController = ArticleController()

	private var data: Pair<String, String>? = null

	private var isFromPush = false

	private var articleTitle: TextView? = null
	private var articleText: TextView? = null
	private var swipeRefreshLayout: SwipeRefreshLayout? = null
	private var scrollView: ScrollView? = null

	override fun onAttach(context: Context)
	{
		super.onAttach(context)
		isFromPush = activity?.isFromPush() ?: false
	}

	override fun onStart()
	{
		super.onStart()
		loadData()
		articleController.viewDidAppear()
	}

	override fun onResume()
	{
		super.onResume()
		articleController.viewResumed()
	}

	override fun onPause()
	{
		super.onPause()
		articleController.viewPaused()
	}

	override fun onStop()
	{
		super.onStop()
		articleController.viewStop()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = super.onCreateView(inflater, container, savedInstanceState)
		articleTitle = view?.findViewById(R.id.articleTitle)
		articleText = view?.findViewById(R.id.articleText)
		swipeRefreshLayout = view?.findViewById(R.id.swiperefreshlayout)
		scrollView = view?.findViewById(R.id.scrollView)

		swipeRefreshLayout?.setOnRefreshListener {
			loadData()
			articleController.reloadContent(this)
		}
		return view
	}

	private fun loadData()
	{
		activity?.readArticlePosition()?.let {
			val articleTitle = resources.getStringArray(R.array.articles_titles)[it]
			val articleText = resources.getStringArray(R.array.articles_text)[it]
			data = Pair(articleTitle, articleText)
			swipeRefreshLayout?.setRefreshing(false)
		}
		articleTitle?.text = String.format(getString(R.string.list_is_from_push), isFromPush, data?.first)
		articleText?.text = data?.second
	}

	fun addContent()
	{
		articleText?.append(data?.second)
		articleController.addMoreContent(this)
	}

	override fun ringPublishingTrackingDidAskForKeepAliveContentStatus(ringPublishingTracking: RingPublishingTracking, contentMetadata: ContentMetadata): KeepAliveContentStatus
	{
		// todo: handle weak reference to fragment inside module
		// Return information about content at given point in time
		// We have to return how big content is and how far the user has scrolled
		return KeepAliveContentStatus(scrollView!!.scrollY.toFloat(), ContentSize(scrollView!!.measuredWidth, scrollView!!.measuredHeight))
	}
}
