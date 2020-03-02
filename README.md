Maps SDK for Android Utility KTX
================================

## Description
This library contains a set of Kotlin extensions for the [Maps SDK for Android Utility Library][amu] enabling you to write more concise, idiomatic Kotlin.

## Requirements
* API level 15+

## Installation

_Coming soon_

## Example Usage

With this KTX library, you should be able to take advantage of several Kotlin language features such as:

> Extension functions

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

> Named parameters and default arguments

_Before_:
```java
GeoJsonLayer layer = new GeoJsonLayer(map, geoJsonFile, null, polygonManager, null, groundOverlayManager);
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

## Support

Encounter an issue while using this library?

If you find a bug or have a feature request, please [file an issue].
Or, if you'd like to contribute, send us a [pull request] and refer to our [code of conduct].

You can also reach us on our [Discord channel].

For more information, check out the detailed guide on the
[Google Developers site][devsite-guide]. 

[file an issue]: https://github.com/googlemaps/android-maps-utils-ktx/issues/new/choose
[pull request]: https://github.com/googlemaps/android-maps-utils-ktx/compare
[code of conduct]: CODE_OF_CONDUCT.md
[Discord channel]: https://discord.gg/hYsWbmk
[devsite-guide]: https://developers.google.com/maps/documentation/android-api/utility/
[amu]: https://github.com/googlemaps/android-maps-utils
