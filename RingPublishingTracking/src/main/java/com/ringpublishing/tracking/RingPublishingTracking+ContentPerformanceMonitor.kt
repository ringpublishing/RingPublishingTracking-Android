/*
 *  Created by Grzegorz Małopolski on 9/21/21, 3:30 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */
package com.ringpublishing.tracking

import com.ringpublishing.tracking.data.ContentMetadata
import com.ringpublishing.tracking.data.ContentPageViewSource
import com.ringpublishing.tracking.delegate.RingPublishingTrackingKeepAliveDataSource
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
	// todo: Implement
	// reportDelegate.reportClick(selectedElementName)
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
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.reportContentClick(selectedElementName: String, publicationUrl: URL)
{
	// todo: Implement
	// reportDelegate.reportClick(selectedElementName)
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
	// todo: Implement
	// reportDelegate.reportUserAction(actionName, actionSubtypeName, parameters)
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
	// todo: Implement
	// reportDelegate.reportUserAction(actionName, actionSubtypeName, parameters)
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
fun RingPublishingTracking.reportPageView(currentStructurePath: List<String>, partiallyReloaded: Boolean)
{
	reportDelegate.reportPageView(currentStructurePath, partiallyReloaded)
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
 * @param contentKeepAliveDataSource: RingPublishingTrackingKeepAliveDataSource
 */
@Suppress("unused", "unused_parameter")
fun RingPublishingTracking.reportContentPageView(
    contentMetadata: ContentMetadata,
    contentPageViewSource: ContentPageViewSource,
    currentStructurePath: List<String>,
    partiallyReloaded: Boolean,
    contentKeepAliveDataSource: RingPublishingTrackingKeepAliveDataSource,
)
{
 	reportDelegate.reportContentPageView(
 		contentMetadata,
 		contentPageViewSource,
        currentStructurePath,
        partiallyReloaded,
        contentKeepAliveDataSource
 	)
}

/**
 * Resumes tracking for currently displayed content
 */
@Suppress("unused")
fun RingPublishingTracking.resumeContentKeepAliveTracking()
{
	// todo: Implement
}

/**
 * Pauses tracking for currently displayed content
 */
@Suppress("unused")
fun RingPublishingTracking.pauseContentKeepAliveTracking()
{
	// todo: Implement
}

/**
 * Stops tracking for currently displayed content
 */
@Suppress("unused")
fun RingPublishingTracking.stopContentKeepAliveTracking()
{
	// todo: Implement
}
