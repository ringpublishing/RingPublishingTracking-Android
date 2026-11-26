package com.ringpublishing.tracking.internal.decorator

import com.ringpublishing.tracking.data.Event

/**
 * The 'IP' and 'IV' a media content keeps from the event that started its playback.
 *
 * Written and read only by [EventDecorator], one event at a time per session, and never under the session lock.
 * The identifiers are replaced as one immutable pair, so a reader never sees 'IP' of one start and 'IV' of another.
 */
internal class IdentifierPin
{

	private class Identifiers(val primary: Any, val secondary: Any)

	@Volatile
	private var identifiers: Identifiers? = null

	fun applyTo(event: Event)
	{
		val pinned = identifiers ?: return
		event.parameters[EventParam.PRIMARY_ID.text] = pinned.primary
		event.parameters[EventParam.SECONDARY_ID.text] = pinned.secondary
	}

	/**
	 * Reads the identifiers the decorators put on [event], so the pin is exactly what went out with it
	 */
	fun capture(event: Event)
	{
		val primary = event.parameters[EventParam.PRIMARY_ID.text]
		val secondary = event.parameters[EventParam.SECONDARY_ID.text]
		identifiers = if (primary != null && secondary != null) Identifiers(primary, secondary) else null
	}
}
