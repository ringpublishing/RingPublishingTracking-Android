package com.ringpublishing.tracking.internal.log

import com.ringpublishing.tracking.listener.LogListener

/**
 * Module logger object
 *
 * This class log by default all module logs on console.
 * You can add your logger to additional handle logs.
 * By default logger have enable log levels: info, warn, error
 *
 * Enable debug logs using method 'debugLogEnabled(enabled: Boolean)'
 * Add own log listener using method 'addLogListener(logListener: LogListener)'
 *
 * Example:
 * Logger.info("My message")
 */
internal object Logger
{

    private val logListeners = mutableSetOf<LogListener>()
    private var debugLogEnabled: Boolean = false

    init
    {
        addLogListener(ConsoleLogger())
    }

    /**
     * Enable or disable debug logs
     *
     * @param enabled turn on logs when is set to true
     */
    fun debugLogEnabled(enabled: Boolean)
    {
        debugLogEnabled = enabled
        info("Set debug log enabled: $enabled")
    }

    /**
     * Add log listener to print logs also by application
     * Is possible to add many loggers that implement 'LogListener'
     * When the same logger will be added twice, then second one will be ignored
     * To remove logger use method 'removeLogListener(logListener: LogListener)'
     */
    internal fun addLogListener(logListener: LogListener)
    {
        logListeners.add(logListener)
    }

    /**
     * Remove application "LogListener" implementation
     */
    internal fun removeLogListener(logListener: LogListener)
    {
        logListeners.remove(logListener)
    }

    /**
     * Debug log
     *
     * @param message will be printed by default on console and also delivered to all added LogListeners
     */
    internal fun debug(message: String)
    {
        if (!debugLogEnabled) return

        logListeners.forEach { logListener -> logListener.debug(message) }
    }

    /**
     * Info log
     *
     * @param message will be printed by default on console and also delivered to all added LogListeners
     */
    internal fun info(message: String) = logListeners.forEach { logListener -> logListener.info(message) }

    /**
     * Warn log
     *
     * @param message will be printed by default on console and also delivered to all added LogListeners
     */
    internal fun warn(message: String) = logListeners.forEach { logListener -> logListener.warn(message) }

    /**
     * Error log
     *
     * @param message will be printed by default on console and also delivered to all added LogListeners
     */
    internal fun error(message: String) = logListeners.forEach { logListener -> logListener.error(message) }
}
