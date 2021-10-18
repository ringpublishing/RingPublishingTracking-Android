/*
 *  Created by Grzegorz Małopolski on 9/22/21, 1:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.extension

import android.app.Activity
import android.content.Intent
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.demo.ArticleActivity

private const val EXTRA_ARTICLE_POSITION = "extraArticlePosition"
private const val EXTRA_PAGE_VIEW_SOURCE = "extraPageViewSource"

fun Activity.create(articlePosition: Int, pageViewSource: ContentPageViewSource? = null) = Intent(this, ArticleActivity::class.java).apply {
	putExtra(EXTRA_ARTICLE_POSITION, articlePosition)
	pageViewSource?.let { putExtra(EXTRA_PAGE_VIEW_SOURCE, pageViewSource.name) }
}

fun Activity.readArticlePosition() = intent?.getIntExtra(EXTRA_ARTICLE_POSITION, 0) ?: 0

fun Activity.isFromPush() = intent?.getStringExtra(EXTRA_PAGE_VIEW_SOURCE)?.equals(ContentPageViewSource.PUSH_NOTIFICATION.name)

fun Activity.getContentPageViewSource(): ContentPageViewSource
{
	return when (intent?.getStringExtra(EXTRA_PAGE_VIEW_SOURCE))
	{
		ContentPageViewSource.PUSH_NOTIFICATION.name -> ContentPageViewSource.PUSH_NOTIFICATION
		ContentPageViewSource.SOCIAL_MEDIA.name -> ContentPageViewSource.SOCIAL_MEDIA
		else -> ContentPageViewSource.DEFAULT
	}
}

fun Activity.startArticleActivity(articlePosition: Int, pageViewSource: ContentPageViewSource? = null) = startActivity(create(articlePosition, pageViewSource))

fun Activity.startArticleActivityFromPush(articlePosition: Int) = startActivity(create(articlePosition, ContentPageViewSource.PUSH_NOTIFICATION))
