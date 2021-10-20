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
import com.ringpublishing.tracking.demo.builder.SampleArticleBuilder
import com.ringpublishing.tracking.demo.controller.ArticleController
import com.ringpublishing.tracking.demo.extension.getContentPageViewSource
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
	private var scrollViewContent: ViewGroup? = null

	override fun onAttach(context: Context)
	{
		super.onAttach(context)
		isFromPush = activity?.isFromPush() ?: false
		activity?.getContentPageViewSource()?.let { articleController.pageViewSource = it }
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		readArticleData()
		articleController.contentViewDidAppear(this)
		articleController.viewDidAppear()
	}

	override fun onStart()
	{
		super.onStart()
		upadteUI()
	}

	override fun onPause()
	{
		super.onPause()
		if (activity?.isFinishing == true)
		{
			articleController.viewStop()
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = super.onCreateView(inflater, container, savedInstanceState)
		articleTitle = view?.findViewById(R.id.articleTitle)
		articleText = view?.findViewById(R.id.articleText)
		swipeRefreshLayout = view?.findViewById(R.id.swipe_refresh_layout)
		scrollView = view?.findViewById(R.id.scrollView)
		scrollViewContent = view?.findViewById(R.id.scrollViewContent)

		swipeRefreshLayout?.setOnRefreshListener {
			loadData()
			articleController.reloadContent(this)
		}
		return view
	}

	private fun loadData()
	{
		readArticleData()
		upadteUI()
	}

	private fun upadteUI()
	{
		articleTitle?.text = String.format(getString(R.string.list_is_from_push), isFromPush, data?.first)
		articleText?.text = data?.second
	}

	private fun readArticleData()
	{
		activity?.readArticlePosition()?.let {
			val articleTitle = resources.getStringArray(R.array.articles_titles)[it]
			val articleText = resources.getStringArray(R.array.articles_text)[it]
			data = Pair(articleTitle, articleText)
			swipeRefreshLayout?.isRefreshing = false
			articleController.loadArticle(SampleArticleBuilder(resources).build(it))
		}
	}

	fun addContent()
	{
		articleText?.append(data?.second)
		articleController.addMoreContent(this)
	}

	override fun ringPublishingTrackingDidAskForKeepAliveContentStatus(ringPublishingTracking: RingPublishingTracking, contentMetadata: ContentMetadata): KeepAliveContentStatus
	{
		return scrollView?.let {

			scrollViewContent?.let { content ->
				KeepAliveContentStatus(it.scrollY, ContentSize(content.measuredWidth, content.measuredHeight))
			} ?: KeepAliveContentStatus(it.scrollY, ContentSize(0, 0))
		} ?: KeepAliveContentStatus(0, ContentSize(0, 0))
	}
}
