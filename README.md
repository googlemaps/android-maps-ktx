![Tests](https://github.com/googlemaps/android-maps-ktx/actions/workflows/test.yml/badge.svg)
![Stable](https://img.shields.io/badge/stability-stable-green)
[![Discord](https://img.shields.io/discord/676948200904589322)](https://discord.gg/hYsWbmk)
![Apache-2.0](https://img.shields.io/badge/license-Apache-blue)

Maps Android KTX
================

## Description
This repository contains Kotlin extensions (KTX) for:
1. The [Maps SDK for Android][maps-sdk]
1. The [Maps SDK for Android Utility Library][amu]

It enables you to write more concise, idiomatic Kotlin. Each set of extensions can be used independently or together.

## Requirements
* Kotlin-enabled project
* Kotlin coroutines
* API level 21+
* An [API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key)

## Installation

```groovy
dependencies {

    // KTX for the Maps SDK for Android library
    implementation 'com.google.maps.android:maps-ktx:4.0.0'

    // KTX for the Maps SDK for Android Utility Library
    implementation 'com.google.maps.android:maps-utils-ktx:4.0.0'
}
```

## Example Usage

With this KTX library, you should be able to take advantage of several Kotlin language features such as extension functions, named parameters and default arguments, destructuring declarations, and coroutines.

### Demo App

<img src="https://developers.google.com/maps/documentation/android-sdk/images/utility-multilayer.png" width="150" align=right>

This repository includes a [demo app](app) that illustrates the use of this KTX library.

To run the demo app, you'll have to:

1. [Get a Maps API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
1. Create a file in the root directory called `secrets.properties` (this file should *NOT* be under version control to protect your API key)
1. Add a single line to `secrets.properties` that looks like `MAPS_API_KEY=YOUR_API_KEY`, where `YOUR_API_KEY` is the API key you obtained in the first step
1. Build and run

### Maps SDK KTX

#### Extension functions

Adding a `Marker`:

_Before_
```java
GoogleMap googleMap = // ...
LatLng sydney = new LatLng(-33.852, 151.211);
MarkerOptions markerOptions = new MarkerOptions()
    .position(Sydney)
    .title("Marker in Sydney");
Marker marker = googleMap.addMarker(markerOptions);
```

_After_
```kotlin
val googleMap = // ...
val sydney = LatLng(-33.852, 151.211)
val marker = googleMap.addMarker {
    position(sydney)
    title("Marker in Sydney")
}
```

#### Coroutines

Accessing a `GoogleMap` instance can be retrieved using coroutines vs. traditional the callback mechanism. The example here demonstrates how you can use this feature alongside with [Lifecycle-aware coroutine scopes][lifecycle] provided in Android’s Architecture Components. To use this, you'll need to add the following to your `build.gradle` dependencies:
`implementation 'androidx.lifecycle:lifecycle-runtime-ktx:<latest-version>'`

_Before_
```java
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

    mapFragment.getMapAsync(new OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            // Access GoogleMap instance here
        }
    });
}
```

_After_
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

    lifecycle.coroutineScope.launchWhenCreated {
        val googleMap = mapFragment?.awaitMap()
    }
}
```

#### Flow

Listing to camera events can be collected via [Kotlin Flow](kotlin-flow). 

_Before_
```java
val googleMap = //...
googleMap.setOnCameraIdleListener = { //... }
googleMap.setOnCameraMoveCanceledListener { //... }
googleMap.setOnCameraMoveListener { //... }
googleMap.setOnCameraMoveStartedListener { //... }
```

_After_
```kotlin
// To be invoked within a coroutine scope
googleMap.cameraIdleEvents().collect { //... }
googleMap.cameraMoveCanceledEvents().collect { //... }
googleMap.cameraMoveEvents().collect { //... }
googleMap.cameraMoveStartedEvents().collect { //... }
```

### Maps SDK for Android Utilities KTX

#### Extension functions

Checking if a `LatLng` is contained within a `Polygon`:

_Before_
```java
Polygon polygon = // some polygon
LatLng latlng = // some latlng
boolean result = PolygonUtil.containsLocation(latlng, polygon.getPoints(), true);
```

_After_
```kotlin
val polygon: Polygon = // some polygon
val latlng: LatLng = // some latlng
val result: Boolean = polygon.contains(latLng)
```

#### Named parameters and default arguments

Creating a `GeoJsonLayer` object:

_Before_
```java
GeoJsonLayer layer = new GeoJsonLayer(
    map, 
    geoJsonFile, 
    null, 
    polygonManager, 
    null, 
    groundOverlayManager
);
```

_After_
```kotlin
val layer = geoJsonLayer(
    map = map,
    geoJsonFile = geoJsonFile,
    polygonManager = polygonManager,
    groundOverlayManager = groundOverlayManager
)
```

#### Destructuring Declarations

Destructuring properties of a `Point`:

_Before_
```java
Point point = new Point(1.0, 2.0);
double x = point.x;
double y = point.y;
```

_After_
```kotlin
val point = Point(1.0, 2.0)
val (x, y) = point
```

## Documentation

You can learn more about all the extensions provided by this library by reading the [reference documents][Javadoc].

## Support

Encounter an issue while using this library?

If you find a bug or have a feature request, please [file an issue].
Or, if you'd like to contribute, send us a [pull request] and refer to our [code of conduct].

You can also reach us on our [Discord channel].

For more information, check out the detailed guide on the
[Google Developers site][devsite-guide]. 

[Discord channel]: https://discord.gg/hYsWbmk
[Javadoc]: https://googlemaps.github.io/android-maps-ktx
[amu]: https://github.com/googlemaps/android-maps-utils
[code of conduct]: CODE_OF_CONDUCT.md
[devsite-guide]: https://developers.google.com/maps/documentation/android-api/utility/
[file an issue]: https://github.com/googlemaps/android-maps-ktx/issues/new/choose
[lifecycle]: https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope
[maps-sdk]: https://developers.google.com/maps/documentation/android-sdk/intro
[maps-v3-sdk]: https://developers.google.com/maps/documentation/android-sdk/v3-client-migration
[pull request]: https://github.com/googlemaps/android-maps-ktx/compare
[kotlin-flow]: https://kotlinlang.org/docs/reference/coroutines/flow.html
