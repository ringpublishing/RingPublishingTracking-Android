/*
 *  Created by Grzegorz Małopolski on 9/21/21, 3:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.delegate.RingPublishingTrackingKeepAliveDataSource
import java.lang.ref.WeakReference
import java.net.URL

/**
 * Click event
 * Reports click event for given app element
 *
 * If user selected icon, element name can be omitted
 * For reporting click leading to article content, see `reportContentClick(...)`
 *
 * @param selectedElementName that user click
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.reportClick(selectedElementName: String?)
{
	val event = eventsFactory.createClickEvent(selectedElementName)
	reportEvent(event)
}

/**
 * Content click event
 * Reports click event which leads to content page
 *
 * If user selected icon, element name can be omitted
 * For reporting click leading to article content, see `reportContentClick(...)`
 *
 * @param selectedElementName that user click
 * @param publicationUrl of content
 * @param contentId content identifier in source system (CMS)
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.reportContentClick(selectedElementName: String, publicationUrl: URL, contentId: String)
{
	val event = eventsFactory.createClickEvent(selectedElementName, publicationUrl, contentId)
	reportEvent(event)
}

/**
 * Reports user action event
 * Passed Map with parameters will be encoded to JSON
 *
 * @param actionName
 * @param actionSubtypeName
 * @param parameters
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.reportUserAction(actionName: String, actionSubtypeName: String, parameters: Map<String, Any>)
{
	val event = eventsFactory.createUserActionEvent(actionName, actionSubtypeName, parametersMap = parameters)
	reportEvent(event)
}

/**
 * Reports user action event
 * Passed parameters as String could be just a plain string value or JSON
 *
 * @param actionName
 * @param actionSubtypeName
 * @param parameters
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.reportUserAction(actionName: String, actionSubtypeName: String, parameters: String)
{
	val event = eventsFactory.createUserActionEvent(actionName, actionSubtypeName, parametersString = parameters)
	reportEvent(event)
}

/**
 * Page view event
 * Reports page view event.
 *
 * - Use this method if you want to report page view event which is not article content.
 * - For reporting article content, see `reportContentPageView(...)`
 *
 * - @param currentStructurePath: Current application structure path used to identify application screen
 * For example "home/sport_list_screen"
 * - @param partiallyReloaded: Pass true if you content was partially reloaded,
 * for example next page of articles on the list was added or paid content was presented after user paid for it on the same screen.
 *
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.reportPageView(currentStructurePath: List<String>, partiallyReloaded: Boolean) = ifInitializedOrWarn {
    keepAliveReporter.stop()

    with(configurationManager)
    {
        updateStructurePath(currentStructurePath, partiallyReloaded = partiallyReloaded)
        updatePartiallyReloaded(partiallyReloaded)
    }

    val event = eventsFactory.createPageViewEvent()
    reportEvent(event)
}

/**
 * Content page view event & keep alive
 *
 * Reports content page view event and immediately starts content keep alive tracking.
 *
 * - Use this method if you want to report article content page view event.
 * - Only one content at the time can be tracked.
 * - Reporting new content page view stops current tracking and start tracking for new content.
 *
 * Parameters:
 * @param contentMetadata: ContentMetadata
 * @param contentPageViewSource: ContentPageViewSource
 * @param currentStructurePath: Current application structure path used to identify application screen
 * For example "home/sport_list_screen"
 * @param partiallyReloaded: Pass true if you content was partially reloaded, for example content was refreshed after in app purchase
 * @param contentKeepAliveDataSource: RingPublishingTrackingKeepAliveDataSource which will be set as WeakReference
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.reportContentPageView(
    contentMetadata: ContentMetadata,
    contentPageViewSource: ContentPageViewSource,
    currentStructurePath: List<String>,
    partiallyReloaded: Boolean,
    contentKeepAliveDataSource: RingPublishingTrackingKeepAliveDataSource,
) = ifInitializedOrWarn {
    keepAliveDelegate = WeakReference(contentKeepAliveDataSource)
    keepAliveReporter.start(contentMetadata, this, partiallyReloaded)

    with(configurationManager)
    {
        updateStructurePath(currentStructurePath, contentMetadata.publicationUrl, contentPageViewSource, partiallyReloaded)
        updatePartiallyReloaded(partiallyReloaded)
    }

    val event = eventsFactory.createPageViewEvent(contentMetadata.contentId, contentMetadata)
    reportEvent(event)
}

/**
 * Resumes tracking for currently displayed content
 */
@Suppress("unused")
fun RingPublishingTracking.resumeContentKeepAliveTracking() = ifInitializedOrWarn {
    keepAliveReporter.resume()
}

/**
 * Pauses tracking for currently displayed content
 */
@Suppress("unused")
fun RingPublishingTracking.pauseContentKeepAliveTracking() = ifInitializedOrWarn {
    keepAliveReporter.pause()
}

/**
 * Stops tracking for currently displayed content
 */
@Suppress("unused")
fun RingPublishingTracking.stopContentKeepAliveTracking() = ifInitializedOrWarn {
    keepAliveReporter.stop()
}
