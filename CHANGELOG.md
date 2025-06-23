1.8.0 Release notes (2025-06-18)
================================

### Changes

* Implementation of Effective page view event
1.7.5 Release notes (2025-05-30)
================================

### Changes

* Fixed issue with nullable typing during initialization check
1.7.4 Release notes (2025-05-29)
================================

### Changes

* Prevented from reporting events if tracking has not been initialized yet
1.7.3 Release notes (2025-03-10)
================================

### Changes

* Update parameter DV path when area is set
1.7.2 Release notes (2025-02-04)
================================

### Changes

* Extract conversion parameters to method 'reportMobileAppTemporaryUserIdReplacedEvent'
1.7.1 Release notes (2025-01-31)
================================

### Changes

* Add new parameters 'fakeUserTermConversionId' and 'realUserTermConversionId' to 'UserIdEventDetailsData'
1.7.0 Release notes (2024-12-17)
================================

### Changes

* Audio event field VE contains hardcoded value = 6 if media type is .tts in every other case value is normal content id
1.6.6 Release notes (2024-12-10)
================================

### Changes

* fakeUserId and realUserId from MobileAppFakeUserIdReplacedEvent are being set as null if they are empty strings.
* fakeUserId and mobileOfferId from PurchaseEvent are being set as null if they are empty strings.

1.6.5 Release notes (2024-11-07)
================================

### Changes

* Audio event field PMU contains value only if it is in UUID format

1.6.4 Release notes (2024-11-05)
================================

### Changes

* FRA (IS_CONTENT_FRAGMENT) event field type has changed from Boolean to Integer

1.6.3 Release notes (2024-10-31)
================================

### Changes

* Setting source_dx empty for empty publicationId


1.6.2 Release notes (2024-10-29)
================================

### Changes

* Add PaidEventParam with 'mobile_offer_id' from offerData used in PurchaseEvent


1.6.1 Release notes (2024-10-18)
================================

### Changes

* Source publication uuid set to content id

1.6.0 Release notes (2024-10-02)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Features

* Added possibility of reporting audio events through 'reportAudioEvent' method  in 'RingPublishingTracking'
* Added classes used to describe reported video event:
    - AudioState class
    - AudioMetadata class
    - AudioEvent enum class
    - AudioContentCategory enum class
    - AudioOutput enum class
    - AudioStreamFormat enum class
    - AudioPlayerVisibilityState enum class

1.5.1 Release notes (2024-09-12)
=============================================================

* Added SDK 35 support

### Changes

* Bumped Gradle version to 8.7
* Bumped Gradle Plugin version to 8.6.0
* Bumped Target SDK version to 35
* Bumped Compile SDK version to 35
* Bumped Build Tools version to 35.0.0
* Updated demo application views and styles
* Disabled html escaping during parsing requests


1.5.0 Release notes (2024-09-12)
=============================================================

* Implemented paid events tracking
* Updated user data tracking with paid data

### Changes

* Changed user data tracking interface and added possibility to set if user is subscribed
    * Added logout function
    * Added updateSSO function
    * Added updateActiveSubscriber function
    * Changed updateUserData function

* Added paid events tracking functions:
    * reportShowOfferEvent
    * reportShowOfferTeaserEvent
    * reportPurchaseClickButtonEvent
    * reportPurchaseEvent
    * reportShowMetricLimitEvent
    * reportLikelihoodScoringEvent
    * reportMobileAppTemporaryUserIdReplacedEvent

1.5.0 Release notes (2024-09-12)
=============================================================

* Implemented paid events tracking
* Updated user data tracking with paid data

### Changes

* Changed user data tracking interface and added possibility to set if user is subscribed
    * Added logout function
    * Added updateSSO function
    * Added updateActiveSubscriber function
    * Changed updateUserData function

* Added paid events tracking functions:
    * reportShowOfferEvent
    * reportShowOfferTeaserEvent
    * reportPurchaseClickButtonEvent
    * reportPurchaseEvent
    * reportShowMetricLimitEvent
    * reportLikelihoodScoringEvent
    * reportMobileAppTemporaryUserIdReplacedEvent

1.4.2 Release notes (2023-12-29)
=============================================================

Update libraries

### Changes

* Update external libraries to latest versions

1.4.1 Release notes (2023-11-21)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Changes

* RDLU parameter is now sent with Event if SSO or ArtemisId is available

1.4.0 Release notes (2023-11-16)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Features

* Extended current tracking identifier by adding new artemis identifier support
* Added proper ArtemisId class describing new identifier

1.3.0 Release notes (2023-11-02)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Features

* Added possibility of reporting video events through 'reportVideoEvent' method  in 'RingPublishingTracking'
* Added classes used to describe reported video event:
    - VideoState class
    - VideoMetadata class
    - VideoEvent enum class
    - VideoAdsConfiguration enum class
    - VideoContentCategory enum class
    - VideoStartMode enum class
    - VideoStreamFormat enum class
    - VideoVisibility classes

1.2.5 Release notes (2023-09-06)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Changes

* Gradle 8
* Android 14
1.2.4 Release notes (2023-08-08)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Changes

* Remove user consents from events data object
* Add user consents to user object

1.2.3 Release notes (2023-02-07)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Changes

* Fixed a bug where valid expiration date for trackingIdentifier was wrongly interpreted as miliseconds

1.2.2 Release notes (2022-04-12)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Changes

* Added additional field to each request, 'RDLC' containing information that events come from native mobile app

1.2.1 Release notes (2022-04-06)
=============================================================

Improvements to the 'RingPublishingTracking' module.

### Fixes

* Track last KeepAlive event before change content page
* In KeepAlive reporter remove observer not in main thread


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

