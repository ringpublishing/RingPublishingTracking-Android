/*
 *  Created by Grzegorz Małopolski on 10/6/21, 11:28 AM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.decorator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.WindowManager
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.log.Logger
import java.lang.ref.WeakReference

internal class WindowSizeDecorator(application: Application) : DeviceScreenDecorator(application)
{

	private val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager

	private var activityWidth: Int = 0
	private var activityHeight: Int = 0

	private var activityReference: WeakReference<Activity>? = null

	private val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener
	{

		override fun onGlobalLayout()
		{
			val activity = activityReference?.get()
			if (activity != null)
			{
				activity.findViewById<View>(android.R.id.content)?.rootView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
				activityWidth = activity.window.decorView.rootView.width
				activityHeight = activity.window.decorView.rootView.height

				Logger.debug("Window size changed width=$activityWidth height=$activityHeight")
			}
		}
	}

	init
	{
		application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks
		{
			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

			override fun onActivityStarted(activity: Activity)
			{
				activityReference = WeakReference(activity)
				activity.findViewById<View>(android.R.id.content)?.rootView?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)
			}

			override fun onActivityResumed(activity: Activity) {}

			override fun onActivityPaused(activity: Activity)
			{
				activity.findViewById<View>(android.R.id.content)?.rootView?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)
				activityReference = null
			}

			override fun onActivityStopped(activity: Activity) {}

			override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

			override fun onActivityDestroyed(activity: Activity) {}
		})
	}

	override fun decorate(event: Event)
	{
		event.add(EventParam.WINDOW_SIZE, getWindowSizeString())
	}

	private fun getWindowSizeString(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
	{
		with(windowManager.currentWindowMetrics)
		{
			val insets = windowInsets.getInsets(WindowInsets.Type.systemBars())
			with(bounds)
			{
				buildSizeString(right - left - insets.left - insets.right, bottom - top - insets.bottom - insets.top)
			}
		}
	} else if (isSizeSet()) buildSizeString(activityWidth, activityHeight) else getScreenSizeString()

	private fun isSizeSet() = activityWidth > 0 && activityHeight > 0
}
