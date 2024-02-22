![RingPublishing](images/ringpublishing_logo.jpeg)

# RingPublishingTracking

Module for tracking events within an application.

## Documentation

The documentation can be found at:

[https://developer.ringpublishing.com/docs/TrackingEngine/index.html](https://developer.ringpublishing.com/docs/TrackingEngine/index.html)

Integration tutorial:

[https://developer.ringpublishing.com/docs/TrackingEngine/getting-started/mobile/integrate-android-app.html](https://developer.ringpublishing.com/docs/TrackingEngine/getting-started/mobile/integrate-android-app.html)

Reference guide:

[https://developer.ringpublishing.com/docs/TrackingEngine/reference/mobile/tracking-android-sdk.html](https://developer.ringpublishing.com/docs/TrackingEngine/reference/mobile/tracking-android-sdk.html)

## Demo

In order to run demo app set `org.gradle.java.home` to Java 11 SDK in file `gradle.properties`.\
Then execute gradle command `./gradlew installDebug` in project root directory.

## Requirements for RingPublishingTracking library

- Java target 1.8
- Android API >= 5.1 (API level 22)
- AndroidX

## Requirements for demo application

- Java target 1.8
- Android API >= 5.1 (API level 22)
- AndroidX
- Recommended Android Studio Arctic Fox (2020.3.1) with:
- SDK Java 11
- Android gradle plugin 7.0.2
- Kotlin gradle plugin 1.5.30
- Android build tools 30.0.2

## Permissions

List permissions used in module:
```ruby
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Installation


### Installation from GitHub Packages

1. Add to your main build.gradle script section to GitHub repository

```ruby
maven {
    name = "RingPublishingTracking-Android"
    url = uri("https://maven.pkg.github.com/ringpublishing/RingPublishingTracking-Android")
    credentials {
        username = "your github username"
        password = "you github access token"
    }
}
```

2. Add to your application project module dependencies section

```ruby
implementation("com.ringpublishing:tracking:1.5.+")
```


### Installation from GitHub source code

1. Checkout code from GitHub
2. Add RingPublishingTracking-Android like module to your project

In settings.gradle add:

```ruby
include ':yourApplication', ':RingPublishingTracking'
```

In your application build.gradle add dependency

```ruby
implementation project(path: ':RingPublishingTracking'))
```

## Usage

Start by importing `RingPublishingTracking`:

```kotlin
import com.ringpublishing.tracking.RingPublishingTracking
```

Then you have access to module object instance:

```kotlin
RingPublishingTracking
```

For detailed example see demo project in `demo` directory or check our documentation.