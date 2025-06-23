/*
 *  Created by Grzegorz Małopolski on 9/23/21, 3:31 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.sample

import android.content.res.Resources
import androidx.annotation.Size
import com.ringpublishing.tracking.demo.R
import com.ringpublishing.tracking.demo.data.SampleArticle
import java.net.URL

class SampleArticleBuilder(private val resources: Resources)
{
	fun build(@Size(min = 1, max = 4) position: Int): SampleArticle
	{
		return SampleArticle(
			resources.getStringArray(R.array.articles_titles)[position],
			resources.getStringArray(R.array.articles_text)[position],
			URL(resources.getStringArray(R.array.articles_url)[position]),
			resources.getIntArray(R.array.articles_paid)[position] > 0,
			resources.getStringArray(R.array.articles_content_ids)[position]
		)
	}
}
