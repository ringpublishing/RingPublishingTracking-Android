/*
 *  Created by Grzegorz Małopolski on 10/7/21, 5:17 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.delegate

import com.ringpublishing.tracking.data.ContentMetadata

internal fun ConfigurationManager.updatePageViewConfiguration(currentStructurePath: List<String>, partiallyReloaded: Boolean)
{
	updateCurrentStructurePath(currentStructurePath)
	currentIsPartialView = partiallyReloaded

	if (partiallyReloaded)
	{
		newSecondaryId()
	}
}

internal fun ConfigurationManager.updateContentPageViewConfiguration(contentMetadata: ContentMetadata, currentStructurePath: List<String>, partiallyReloaded: Boolean)
{
	updateCurrentPublicationUrl(contentMetadata.publicationUrl)
	updateCurrentStructurePath(currentStructurePath)
	currentIsPartialView = partiallyReloaded

	if (partiallyReloaded) newSecondaryId() else newPrimaryId()
}
