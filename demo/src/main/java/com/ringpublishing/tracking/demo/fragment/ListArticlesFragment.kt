/*
 *  Created by Grzegorz Małopolski on 9/22/21, 1:04 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.SimpleAdapter
import androidx.fragment.app.ListFragment
import com.ringpublishing.tracking.demo.R
import com.ringpublishing.tracking.demo.builder.SampleArticleBuilder
import com.ringpublishing.tracking.demo.controller.ListArticlesController
import com.ringpublishing.tracking.demo.data.SampleArticle
import com.ringpublishing.tracking.demo.extension.startArticleActivity
import kotlin.collections.set

class ListArticlesFragment : ListFragment(), OnItemClickListener
{

	private val listArticlesController = ListArticlesController()

	private var articles: List<SampleArticle>? = null

	private val titleLabel = "title"

	private val paidLabel = "paid"

	var data = ArrayList<HashMap<String, String?>>()

	override fun onAttach(context: Context)
	{
		super.onAttach(context)
		val sampleArticleBuilder = SampleArticleBuilder(context.resources)
		articles = listOf(sampleArticleBuilder.build(0), sampleArticleBuilder.build(1), sampleArticleBuilder.build(2))
	}

	override fun onDetach()
	{
		articles = null
		super.onDetach()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		data.clear()
		articles?.forEach {
			val map = HashMap<String, String?>()
			map[titleLabel] = it.title
			map[paidLabel] = "Is paid: ${it.contentWasPaidFor}"
			data.add(map)
		}

		val from = arrayOf(titleLabel, paidLabel)
		val to = intArrayOf(R.id.itemTitle, R.id.itemPaid)

		listAdapter = SimpleAdapter(activity, data, R.layout.fragment_list_articles, from, to)
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onStart()
	{
		super.onStart()
		listView.onItemClickListener = this
	}

	override fun onResume()
	{
		super.onResume()
		listArticlesController.viewDidAppear()
	}

	override fun onPause()
	{
		super.onPause()
		if (activity?.isFinishing == true)
		{
			listArticlesController.viewStop()
		}
	}

	override fun onStop()
	{
		listView.onItemClickListener = null
		super.onStop()
	}

	override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
	{
		listArticlesController.viewStop()

		activity?.startArticleActivity(position)

		articles?.let {
			val sampleArticle = it[position]
			sampleArticle.let { article ->
				listArticlesController.listItemClick(article.title, article.publicationUrl, article.publicationId)
			}
		}
	}
}
