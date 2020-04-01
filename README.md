![Tests](https://github.com/googlemaps/android-maps-ktx/workflows/Run%20unit%20tests/badge.svg)
![Beta](https://img.shields.io/badge/stability-beta-yellow)
[![Discord](https://img.shields.io/discord/676948200904589322)](https://discord.gg/hYsWbmk)
![Apache-2.0](https://img.shields.io/badge/license-Apache-blue)

Maps Android KTX
================

## Description
This repository contains Kotlin extensions (KTX) for:
1. The [Maps SDK for Android][maps-sdk]
1. The [Maps SDK for Android Utility library][amu]

It enables you to write more concise, idiomatic Kotlin. Each set of extensions can be used independently or together.

## Requirements
* API level 15+

## Installation

```groovy
dependencies {

    // KTX for the Maps SDK library
    implementation 'com.google.maps.android:maps-ktx:0.3.1'

    // KTX for the Maps SDK for Android Utility library
    implementation 'com.google.maps.android:maps-utils-ktx:0.3.1'
}
```

## Example Usage

With this KTX library, you should be able to take advantage of several Kotlin language features such as extension functions, named parameters and default arguments, destructuring declarations, and coroutines.

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
Val googleMap = // ...
val sydney = LatLng(-33.852, 151.211)
val marker = googleMap.addMarker {
    position(sydney)
    title("Marker in Sydney")
}
```

#### Coroutines

Accessing a `GoogleMap` instance can be retrieved using coroutines vs. traditional the callback mechanism. The example here demonstrates how you can use this feature alongside with [Lifecycle-aware coroutine scopes][lifecycle] provided in Android’s Architecture Components. To use this, you'll need to add the following to your `build.gradle` dependencies:
`implementation 'androidx.lifecycle:lifecycle-runtime-ktx:<latest-version>'`


**NOTE**: This is an experimental feature and can only be used by using the `MapsExperimentalFeature` annotation class.

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
@MapsExperimentalFeature
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

    lifecycle.coroutineScope.launchWhenCreated {
        val googleMap = mapFragment?.awaitMap()
    }
}
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

You can learn more about all the extensions provided by this library by reading the [reference documents][Javadoc].

## Support

Encounter an issue while using this library?

If you find a bug or have a feature request, please [file an issue].
Or, if you'd like to contribute, send us a [pull request] and refer to our [code of conduct].

You can also reach us on our [Discord channel].

For more information, check out the detailed guide on the
[Google Developers site][devsite-guide]. 

[Discord channel]: https://discord.gg/hYsWbmk
[Javadoc]: https://googlemaps.github.io/android-maps-ktx/maps-utils-ktx/
[amu]: https://github.com/googlemaps/android-maps-utils
[code of conduct]: CODE_OF_CONDUCT.md
[devsite-guide]: https://developers.google.com/maps/documentation/android-api/utility/
[file an issue]: https://github.com/googlemaps/android-maps-ktx/issues/new/choose
[lifecycle]: https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope
[maps-sdk]: https://developers.google.com/maps/documentation/android-sdk/intro
[pull request]: https://github.com/googlemaps/android-maps-ktx/compare
