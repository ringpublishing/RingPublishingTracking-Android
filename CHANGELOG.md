1.2.0 Release notes (2022-03-23)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Changes

* Added required parameter 'contentId' to 'ContentMetadata'
* Renamed 'publicationId' parameter to 'contentId' in methods:
    - reportContentClick

1.1.2 Release notes (2022-03-11)
=============================================================

### Fixes

* Fix date parsing. Now Long value is persisted instead of Date

1.1.1 Release notes (2022-02-21)
=============================================================

Add user id parameter to Event

### Changes

* Add new internal parameter user id to Event request with name 'IZ'

1.1.0 Release notes (2022-01-27)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Features

* Added 'TrackingIdentifierError' enum
* Added 'ringPublishingTrackingDidFailToRetrieveTrackingIdentifier' method to 'RingPublishingTrackingDelegate' interface
        This method will be called every time there was an attempt to fetch tracking identifier but it failed during module initialization (or when another attempt to fetch tracking identifier was performed)

1.0.2 Release notes (2022-01-04)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Changes

* Assign 'trackingIdentifier' variable on start and notify 'RingPublishingTrackingDelegate'.
1.0.1 Release notes (2021-11-05)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Features

* Added 'ErrorEvent' which is being sent in case the original event was incorrect
* Removing reported events from the queue in case of client's side issue while sending events

### Changes

* Use play services library to read advertisement id instead of androidx advertisement library
1.0.0 Release notes (2021-10-21)
=============================================================

Fully functional 'RingPublishingTracking' module release.

### Features

* Added 'email' parameter to the `updateUserData` method
* 'trackingIdentifier' property on `RingPublishingTracking` object is now a struct and contains `identifier` and `expirationDate` properties
* 'publicationId' parameter was added to `reportContentClick` method
* Added `reportContentClick` method variant with `aureusOfferId` parameter which should be used when interacting with Aureus recommendations

### Changes

* SDK initialization now takes as parameter "Application" object instead of Context.
* There was wrong type declared for "applicationDefaultStructurePath" in "RingPublishingTrackingConfiguration", parameter type was changes from String to List

0.1.0 Release notes (2021-09-30)
=============================================================

First 'RingPublishingTracking' module release. This version is not fully functional yet, but has available complete public interface to use.

### Features

* Report application events, such as:
   - 'Click' event
   - 'User Action' event
   - 'Page View' event
   - 'Keep Alive' event
   - 'Aureus offers impression' event
   - 'Generic' event (not predefined in SDK)
* Update global tracking properties, such as:
   - user data
   - advertisement area
* Enable debug mode or opt-out mode

