/*
 *  Created by Grzegorz Małopolski on 9/22/21, 3:15 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.dialog

import android.app.AlertDialog
import android.content.Context
import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.demo.R
import com.ringpublishing.tracking.pauseContentKeepAliveTracking
import com.ringpublishing.tracking.resumeContentKeepAliveTracking

class ArticleDialog
{

	fun show(context: Context)
	{
		val dialog: AlertDialog = AlertDialog.Builder(context)
			.setTitle(context.getString(R.string.article_dialog_title))
			.setMessage(context.getString(R.string.article_dialog_message))
			.setOnCancelListener { dialog ->
				dialog.dismiss()
				RingPublishingTracking.resumeContentKeepAliveTracking()
			}.create()
		dialog.show()

		RingPublishingTracking.pauseContentKeepAliveTracking()
	}
}
