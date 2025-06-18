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
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentSize
import com.ringpublishing.tracking.data.KeepAliveContentStatus
import com.ringpublishing.tracking.delegate.RingPublishingTrackingKeepAliveDataSource
import com.ringpublishing.tracking.demo.R
import com.ringpublishing.tracking.demo.controller.ArticleController
import com.ringpublishing.tracking.demo.extension.getContentPageViewSource
import com.ringpublishing.tracking.demo.extension.isFromPush
import com.ringpublishing.tracking.demo.extension.readArticlePosition
import com.ringpublishing.tracking.demo.sample.SampleArticleBuilder

class ArticleFragment : Fragment(R.layout.fragment_article), RingPublishingTrackingKeepAliveDataSource {

    private val articleController = ArticleController()

    private var data: Pair<String, String>? = null
    private var articleDataText: String? = ""

    private var isFromPush = false

    private var articleTitle: TextView? = null
    private var articleText: TextView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var scrollView: ScrollView? = null
    private var scrollViewContent: ViewGroup? = null
    private var epvAudioButton: Button? = null
    private var epvVideoButton: Button? = null
    private var epvChatButton: Button? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isFromPush = activity?.isFromPush() ?: false
        activity?.getContentPageViewSource()?.let { articleController.pageViewSource = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readArticleData()
        articleController.contentViewDidAppear(this)
    }

    override fun onPause() {
        super.onPause()
        if (activity?.isFinishing == true) {
            articleController.viewStop()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        epvAudioButton = view?.findViewById(R.id.epvAudioButton)
        epvVideoButton = view?.findViewById(R.id.epvVideoButton)
        epvChatButton = view?.findViewById(R.id.epvChatButton)
        initEpvButtonsListeners()
        initInsets()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    private fun loadData() {
        readArticleData()
        updateUI()
    }

    private fun updateUI() {
        articleTitle?.text = String.format(getString(R.string.list_is_from_push), isFromPush.toString(), data?.first)
        articleText?.text = data?.second
    }

    private fun readArticleData() {
        activity?.readArticlePosition()?.let {
            val articleTitle = resources.getStringArray(R.array.articles_titles)[it]
            val articleText = resources.getStringArray(R.array.articles_text)[it]
            data = Pair(articleTitle, articleText)
            articleDataText = articleText
            swipeRefreshLayout?.isRefreshing = false
            articleController.loadArticle(SampleArticleBuilder(resources).build(it))
        }
    }

    fun addContent() {
        data = Pair(data?.first ?: "", data?.second + articleDataText)
        articleText?.append(data?.second)

        articleController.addMoreContent(this)
    }

    override fun ringPublishingTrackingDidAskForKeepAliveContentStatus(ringPublishingTracking: RingPublishingTracking, contentMetadata: ContentMetadata): KeepAliveContentStatus {
        return scrollView?.let {

            scrollViewContent?.let { content ->
                KeepAliveContentStatus(it.scrollY, ContentSize(content.measuredWidth, content.measuredHeight))
            } ?: KeepAliveContentStatus(it.scrollY, ContentSize(0, 0))
        } ?: KeepAliveContentStatus(0, ContentSize(0, 0))
    }

    private fun initEpvButtonsListeners() {
        epvAudioButton?.setOnClickListener {
            articleController.reportEffectivePageView(
                effectivePageViewComponentSource = "audio",
                effectivePageViewTriggerSource = "play"
            )
        }
        epvVideoButton?.setOnClickListener {
            articleController.reportEffectivePageView(
                effectivePageViewComponentSource = "video",
                effectivePageViewTriggerSource = "play"
            )
        }
        epvAudioButton?.setOnClickListener {
            articleController.reportEffectivePageView(
                effectivePageViewComponentSource = "chat",
                effectivePageViewTriggerSource = "summary"
            )
        }
    }

    private fun initInsets() {
        scrollView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v: View, insets: WindowInsetsCompat ->
                val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
                val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                v.setPadding(0, statusBarHeight, 0, navBarHeight)
                WindowInsetsCompat.Builder(insets).build()
            }
        }
    }
}
