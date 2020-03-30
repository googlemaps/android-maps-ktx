![Tests](https://github.com/googlemaps/android-maps-ktx/workflows/.github/workflows/test.yml/badge.svg)
![Beta](https://img.shields.io/badge/stability-beta-yellow)
[![Discord](https://img.shields.io/discord/676948200904589322)](https://discord.gg/hYsWbmk)
![Apache-2.0](https://img.shields.io/badge/license-Apache-blue)

Maps Android KTX
================

## Description
This repository contains a set a of Kotlin extensions (KTX) for the [Maps SDK for Android Library][maps-sdk] and the [Utility][amu] libraries enabling you to write more concise, idiomatic Kotlin.

## Requirements
* API level 15+

## Installation

```groovy
dependencies {

    // KTX for the maps SDK library - Coming Soon
// implementation 'com.google.maps.android:maps-ktx:<future-version>'

    // KTX for the utility library
    implementation 'com.google.maps.android:maps-utils-ktx:0.2'
}
```

## Example Usage

With this KTX library, you should be able to take advantage of several Kotlin language features such as:

#### Extension functions

_Before_:
```java
Polygon polygon = // some polygon
LatLng latlng = // some latlng
boolean result = PolygonUtil.containsLocation(latlng, polygon.getPoints(), true);
```

_After_:
```kotlin
val polygon: Polygon = // some polygon
val latlng: LatLng = // some latlng
val result: Boolean = polygon.contains(latLng)
```

#### Named parameters and default arguments

_Before_:
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

_After_:
```kotlin
val layer = GeoJsonLayer(
    map = map,
    geoJsonFile = geoJsonFile,
    polygonManager = polygonManager,
    groundOverlayManager = groundOverlayManager
)
```

#### Destructuring Declarations

_Before_:
```java
Point point = new Point(1.0, 2.0);
double x = point.x;
double y = point.y;
```

_After_:
```kotlin
val point = Point(1.0, 2.0)
val (x, y) = point
```

You can learn more by reading the [Javadoc].

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
[maps-sdk]: https://developers.google.com/maps/documentation/android-sdk/intro
[pull request]: https://github.com/googlemaps/android-maps-ktx/compare
