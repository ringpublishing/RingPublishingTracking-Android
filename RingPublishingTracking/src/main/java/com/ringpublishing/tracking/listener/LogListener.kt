/*
 *  Created by Grzegorz Małopolski on 9/21/21, 4:33 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking.listener

/**
 * Interface used to implement custom log listener class
 *
 * To add custom logger class to Logger use method 'Logger.addListener(logListener: LogListener)'
 * To remove custom logger class from Logger use method 'Logger.removeListener(logListener: LogListener)'
 *
 * Example:
 * Logger.addListener(ApplicationCustomLogger())
 */
interface LogListener
{

    /**
     * Debug log
     * @param message is debug log message from module
     */
    fun debug(message: String)

    /**
     * Info log
     * @param message is info log message from module
     */
    fun info(message: String)

    /**
     * Warn log
     * @param message is warn log message from module
     */
    fun warn(message: String)

    /**
     * Error log
     * @param message is error log message from module
     */
    fun error(message: String)
}
