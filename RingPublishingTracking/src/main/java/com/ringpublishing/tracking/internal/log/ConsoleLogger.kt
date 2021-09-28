package com.ringpublishing.tracking.internal.log

import android.util.Log
import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.listener.LogListener

internal class ConsoleLogger : LogListener
{

	private val tag = RingPublishingTracking.javaClass.simpleName

	override fun debug(message: String)
	{
		Log.d(tag, message)
	}

	override fun info(message: String)
	{
		Log.i(tag, message)
	}

	override fun warn(message: String)
	{
		Log.w(tag, message)
	}

	override fun error(message: String)
	{
		Log.e(tag, message)
	}
}
