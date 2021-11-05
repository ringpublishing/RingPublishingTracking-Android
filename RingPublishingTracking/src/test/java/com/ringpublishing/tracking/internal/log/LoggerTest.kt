/*
 *  Created by Grzegorz Małopolski on 10/28/21, 3:46 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.log

import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.listener.LogListener
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LoggerTest
{

    @MockK
    var logListener = object : LogListener
    {
        override fun debug(message: String) = Unit

        override fun info(message: String) = Unit

        override fun warn(message: String) = Unit

        override fun error(message: String) = Unit
    }

    @Before
    fun before()
    {
        MockKAnnotations.init(this, relaxUnitFun = true)
	    Logger.addLogListener(logListener)
	    Logger.debugLogEnabled(false)
    }

    @Test
    fun debug_DebugLogCalled_Then_LogListenerDebugNotCalled()
    {
	    Logger.debug("message")
        verify(exactly = 0) { logListener.debug("message") }
    }

    @Test
    fun debug_DebugEnabledAndDebugLogCalled_Then_LogListenerDebugCalled()
    {
	    Logger.debugLogEnabled(true)
	    Logger.debug("message")
        verify { logListener.debug("message") }
    }

    @Test
    fun debug_DebugDisabledAndDebugLogCalled_Then_LogListenerDebugCalled()
    {
	    Logger.debugLogEnabled(true)
	    Logger.debugLogEnabled(false)
	    Logger.debug("message")
        verify(exactly = 0) { logListener.debug("message") }
    }

    @Test
    fun info_InfoLogCalled_Then_LogListenerInfoCalled()
    {
	    Logger.info("message")
        verify { logListener.info("message") }
    }

    @Test
    fun warn_WarnLogCalled_Then_LogListenerWarnCalled()
    {
	    Logger.warn("message")
        verify { logListener.warn("message") }
    }

    @Test
    fun error_ErrorLogCalled_Then_LogListenerErrorCalled()
    {
	    Logger.error("message")
        verify { logListener.error("message") }
    }

    @Test
    fun error_ErrorLogCalled_Then_NoOtherListenMethodCalled()
    {
	    Logger.error("message")
        verify(exactly = 0) { logListener.debug("message") }
        verify(exactly = 0) { logListener.info("message") }
        verify(exactly = 0) { logListener.warn("message") }
    }

    @Test
    fun removeLog_ErrorLogCalled_Then_NoListenMethodCalled()
    {
	    Logger.removeLogListener(logListener)
	    Logger.error("message")
        verify(exactly = 0) { logListener.debug("message") }
        verify(exactly = 0) { logListener.info("message") }
        verify(exactly = 0) { logListener.warn("message") }
        verify(exactly = 0) { logListener.error("message") }
    }
}
