/*
 *  Created by Grzegorz Małopolski on 9/22/21, 1:03 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.demo.fragment

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.ringpublishing.tracking.demo.R
import com.ringpublishing.tracking.demo.controller.NoContentController

class NoContentFragment : Fragment(R.layout.fragment_no_content)
{

	private val noContentController = NoContentController()

	private val data = "<html>\n" +
			"<body>\n" +
			"\n" +
			"<center><p>This is 'no content view', but we still should track it and report 'pageView' for it " +
			"and update application structure path.</p></center>\n" +
			"\n" +
			"</body>\n" +
			"</html>"

	private val webViewClient = object : WebViewClient()
	{
		override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean
		{
			view.loadUrl(url)
			return false
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	override fun onResume()
	{
		super.onResume()
		val webView = activity?.findViewById<WebView>(R.id.noContentWebView)
		webView?.settings?.javaScriptEnabled = true
		webView?.webViewClient = webViewClient
		webView?.loadData(data, "text/html", Charsets.UTF_8.toString())

		// On screen without content only pageView event is send.
		// Content events are not sent, because is no native content
		noContentController.viewDidAppear()
	}
}
