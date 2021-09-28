/*
 *  Created by Grzegorz Małopolski on 9/22/21, 1:02 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.ringpublishing.tracking.demo.dialog.ArticleDialog
import com.ringpublishing.tracking.demo.extension.readArticlePosition
import com.ringpublishing.tracking.demo.extension.startArticleActivityFromPush
import com.ringpublishing.tracking.demo.fragment.ArticleFragment

class ArticleActivity : AppCompatActivity()
{

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_article)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	override fun onSupportNavigateUp(): Boolean
	{
		onBackPressed()
		return true
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean
	{
		menuInflater.inflate(R.menu.article_action_bar_menu, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		when (item.itemId)
		{
			R.id.articleMenuPush ->
			{
				startArticleActivityFromPush(readArticlePosition())
			}
			R.id.articleMenuModal ->
			{
				ArticleDialog().show(this)
			}
			R.id.articleMenuAddContent ->
			{
				getArticleFragment()?.addContent()
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun getArticleFragment() = (supportFragmentManager.findFragmentById(R.id.articleFragment) as ArticleFragment?)
}
