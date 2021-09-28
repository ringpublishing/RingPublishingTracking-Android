/*
 *  Created by Grzegorz Małopolski on 9/22/21, 1:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.extension

import android.app.Activity
import android.content.Intent
import com.ringpublishing.tracking.demo.ArticleActivity

private const val EXTRA_ARTICLE_POSITION = "extraArticlePosition"
private const val EXTRA_FROM_PUSH = "extraFromPush"

fun Activity.create(articlePosition: Int) = Intent(this, ArticleActivity::class.java).apply {
	putExtra(EXTRA_ARTICLE_POSITION, articlePosition)
}

fun Activity.createFromPush(articlePosition: Int) = create(articlePosition).apply {
	putExtra(EXTRA_FROM_PUSH, true)
}

fun Activity.readArticlePosition() = intent?.getIntExtra(EXTRA_ARTICLE_POSITION, 0) ?: 0

fun Activity.isFromPush() = intent?.getBooleanExtra(EXTRA_FROM_PUSH, false)

fun Activity.startArticleActivity(articlePosition: Int) = startActivity(create(articlePosition))

fun Activity.startArticleActivityFromPush(articlePosition: Int) = startActivity(createFromPush(articlePosition))
