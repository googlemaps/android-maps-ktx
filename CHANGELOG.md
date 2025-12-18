# Changelog

## [6.0.0](https://github.com/googlemaps/android-maps-ktx/compare/v5.2.2...v6.0.0) (2025-12-18)


### ⚠ BREAKING CHANGES

* Since android-maps-utils v3.2.0 introduced z-index to marker clusters, this change propagates that change. See release notes from android-maps-utils v3.2.0 https://github.com/googlemaps/android-maps-utils/releases/tag/v3.2.0
* updated Gradle version, plugins and libraries ([#212](https://github.com/googlemaps/android-maps-ktx/issues/212))
* awaitAnimation renamed to awaitAnimateCamera
* compile-time and run-time dependencies have changed.
* Moved KTX for utils from com.google.maps.android.ktx to com.google.maps.android.ktx.utils to avoid package name conflicts with the new maps-ktx module.

### Features

* Add Kotlin Flow extensions for maps-ktx and maps-v3-ktx ([#144](https://github.com/googlemaps/android-maps-ktx/issues/144)) ([e5cc99f](https://github.com/googlemaps/android-maps-ktx/commit/e5cc99f93a4956837f479861d274588d8dc5ce5d))
* Add KTX addMarker helper ([#36](https://github.com/googlemaps/android-maps-ktx/issues/36)) ([8999aa1](https://github.com/googlemaps/android-maps-ktx/commit/8999aa1dc34376d88cce6a1bff469805325a5560))
* Add KTX for Utility V3 Beta ([#56](https://github.com/googlemaps/android-maps-ktx/issues/56)) ([4f23cc8](https://github.com/googlemaps/android-maps-ktx/commit/4f23cc8a5eddad927a353da477e89ac752daa10b))
* Add model helpers to Manager objects. ([#42](https://github.com/googlemaps/android-maps-ktx/issues/42)) ([4f3f582](https://github.com/googlemaps/android-maps-ktx/commit/4f3f582c04d2201adce6ace7a5429b4a9106df9c))
* Add suspending awaitAnimation() ([#112](https://github.com/googlemaps/android-maps-ktx/issues/112)) ([2afde31](https://github.com/googlemaps/android-maps-ktx/commit/2afde311c53eac324540d242a61f336e15393dcd))
* Add suspending fun to MapView and StreetViewPanorama ([#71](https://github.com/googlemaps/android-maps-ktx/issues/71)) ([7245873](https://github.com/googlemaps/android-maps-ktx/commit/7245873d16213acc04a64dadb4a6fca86343f4b4))
* Adding ability to publish to maven central ([#5](https://github.com/googlemaps/android-maps-ktx/issues/5)) ([5cfa6b1](https://github.com/googlemaps/android-maps-ktx/commit/5cfa6b1e349817c30b0fb838cec5cbc1d5aec1ed))
* Adding cameraEvents() method to consume camera events using Kotlin Flow ([#78](https://github.com/googlemaps/android-maps-ktx/issues/78)) ([4eb4f4e](https://github.com/googlemaps/android-maps-ktx/commit/4eb4f4ece6f0ce871b7a9b3d44b9e7cc4660e90f))
* Adding KTX for Maps SDK V3 Beta ([#51](https://github.com/googlemaps/android-maps-ktx/issues/51)) ([e2d787d](https://github.com/googlemaps/android-maps-ktx/commit/e2d787d18f0bfc69b380b42b7ad5070fe85362da))
* Adding KTX to Maps SDK ([#21](https://github.com/googlemaps/android-maps-ktx/issues/21)) ([3313167](https://github.com/googlemaps/android-maps-ktx/commit/3313167e9c78daf0a581c42d45bc3ebb8211839d))
* Adding PolyUtil extensions to List&lt;LatLng&gt; ([#16](https://github.com/googlemaps/android-maps-ktx/issues/16)) ([d4cd6f5](https://github.com/googlemaps/android-maps-ktx/commit/d4cd6f5b312a77e91935ac56add90f1b459573b8))
* Adding suspending functions awaitSnapshot and awaitMapLoad. ([#119](https://github.com/googlemaps/android-maps-ktx/issues/119)) ([82b6d19](https://github.com/googlemaps/android-maps-ktx/commit/82b6d19a4fb8d82ab53c58672d04b0795ab8682e))
* Bump android-maps-utils to 1.1.0 ([#38](https://github.com/googlemaps/android-maps-ktx/issues/38)) ([5b52d2c](https://github.com/googlemaps/android-maps-ktx/commit/5b52d2cbe828f2938fd104ff3ddfb69fcbd5edd9))
* Bump Kotlin and coroutines to 1.6 ([#178](https://github.com/googlemaps/android-maps-ktx/issues/178)) ([d794acf](https://github.com/googlemaps/android-maps-ktx/commit/d794acfb945013218c0925b82bf9b219eb964cc6))
* Bump Kotlin to 1.5.30 and KotlinX Coroutines to 1.5.1 ([#162](https://github.com/googlemaps/android-maps-ktx/issues/162)) ([df90327](https://github.com/googlemaps/android-maps-ktx/commit/df90327e06ebc96ee4f0f5366c6d9371f669e001))
* Create maps-ktx module ([#20](https://github.com/googlemaps/android-maps-ktx/issues/20)) ([597a70c](https://github.com/googlemaps/android-maps-ktx/commit/597a70cfa23efde95a1d9b827a420ee1081c3dfc))
* Enable LatLng destructuring. ([#39](https://github.com/googlemaps/android-maps-ktx/issues/39)) ([73b1d8a](https://github.com/googlemaps/android-maps-ktx/commit/73b1d8a836cfefa0c93856829e55289f6eb0f9e3))
* increases Play Services Maps version ([#312](https://github.com/googlemaps/android-maps-ktx/issues/312)) ([2ea5682](https://github.com/googlemaps/android-maps-ktx/commit/2ea5682a429ce41ba362bc52a4d3fff48eaff4f8))
* Modifying transitive dependencies ([#75](https://github.com/googlemaps/android-maps-ktx/issues/75)) ([08de8e6](https://github.com/googlemaps/android-maps-ktx/commit/08de8e69447f311ddb860bcbeeded6e2b616c78b))
* Remove MapsExperimentalFeature annotation requirement. ([#62](https://github.com/googlemaps/android-maps-ktx/issues/62)) ([310704e](https://github.com/googlemaps/android-maps-ktx/commit/310704e4aa296d1dc3e015811dceb1a5613add0a))
* Update gradle version and Kotlin. ([#103](https://github.com/googlemaps/android-maps-ktx/issues/103)) ([8cc48ad](https://github.com/googlemaps/android-maps-ktx/commit/8cc48ade47c0f36d890585d70e396ec0447659d8))
* Update Maps to 18.0.0 ([#172](https://github.com/googlemaps/android-maps-ktx/issues/172)) ([8f5b46d](https://github.com/googlemaps/android-maps-ktx/commit/8f5b46d5595664c508bc2f4ad7146457c1cc3a54))
* updated to Maps SDK 19.0.0 ([#260](https://github.com/googlemaps/android-maps-ktx/issues/260)) ([9a69770](https://github.com/googlemaps/android-maps-ktx/commit/9a69770af24a9bf82468f598a6c2c9812c9fac4f))


### Bug Fixes

* Add dependencies of maps-utils-ktx in pom. ([#14](https://github.com/googlemaps/android-maps-ktx/issues/14)) ([d2f14e4](https://github.com/googlemaps/android-maps-ktx/commit/d2f14e44e62ddca7420f715903a3b096df2ecb0a))
* Add missing reason for cameraMoveStartedEvents() flow ([#169](https://github.com/googlemaps/android-maps-ktx/issues/169)) ([2166001](https://github.com/googlemaps/android-maps-ktx/commit/2166001601a4731018e27d68ab15244119ecc42b)), closes [#168](https://github.com/googlemaps/android-maps-ktx/issues/168)
* Assign unique package names to libs. ([#137](https://github.com/googlemaps/android-maps-ktx/issues/137)) ([49ef442](https://github.com/googlemaps/android-maps-ktx/commit/49ef4428d173530e63941789faa0629992ffb17f))
* bring back Java 8 compatibility ([#267](https://github.com/googlemaps/android-maps-ktx/issues/267)) ([fded4c0](https://github.com/googlemaps/android-maps-ktx/commit/fded4c0dc0a63f240cacbba7f115085974588078))
* Corrected broken links in Support section of README.md ([#355](https://github.com/googlemaps/android-maps-ktx/issues/355)) ([838913b](https://github.com/googlemaps/android-maps-ktx/commit/838913bc8d70cc7a3715e5f927837b9fa7ba3ae2))
* fixed release config ([#313](https://github.com/googlemaps/android-maps-ktx/issues/313)) ([03eb8c0](https://github.com/googlemaps/android-maps-ktx/commit/03eb8c02c8262f9f5642f79dc8c6216a09d31e95))
* next repositories inside publishing ([38e8dbc](https://github.com/googlemaps/android-maps-ktx/commit/38e8dbc455e921c8d5220cd4c56ebae7e43670fc))
* Remove listeners in awaitClose block in GoogleMaps.cameraEvents(): Flow&lt;CameraEvent&gt; implementation and remove unnecessary inline. ([#82](https://github.com/googlemaps/android-maps-ktx/issues/82)) ([7756605](https://github.com/googlemaps/android-maps-ktx/commit/77566051a01b5539b9d90586146a28c64e1d98b1))
* Rename awaitAnimation to awaitAnimateCamera ([#126](https://github.com/googlemaps/android-maps-ktx/issues/126)) ([770b068](https://github.com/googlemaps/android-maps-ktx/commit/770b068a28c6a2c46427be0f3522a6ebafb3f360))
* Silently catch exception if channel is closed. ([#86](https://github.com/googlemaps/android-maps-ktx/issues/86)) ([8a8fc7c](https://github.com/googlemaps/android-maps-ktx/commit/8a8fc7cd734c5df05de2e58a30764243f7760308))
* update gradle wrapper to 9.1.0 ([#345](https://github.com/googlemaps/android-maps-ktx/issues/345)) ([52fbecf](https://github.com/googlemaps/android-maps-ktx/commit/52fbecf41040a232993b96dfc37d18155debed50))
* updated Gradle version, plugins and libraries ([#212](https://github.com/googlemaps/android-maps-ktx/issues/212)) ([d622499](https://github.com/googlemaps/android-maps-ktx/commit/d622499d8d780f74eb40bdd98e30d861fbe88612))
* upgrade build dependencies ([#216](https://github.com/googlemaps/android-maps-ktx/issues/216)) ([ac28a84](https://github.com/googlemaps/android-maps-ktx/commit/ac28a841909b12376252cc5efb99893a0f657cb1))
* Use api instead of implementation. ([#10](https://github.com/googlemaps/android-maps-ktx/issues/10)) ([ac40f46](https://github.com/googlemaps/android-maps-ktx/commit/ac40f460a22a6d36eafb7b01062900e267f82e8c))
* Use suspendCancellationCoroutine instead of suspendCoroutine ([#98](https://github.com/googlemaps/android-maps-ktx/issues/98)) ([483215d](https://github.com/googlemaps/android-maps-ktx/commit/483215d2cabc9e5ef1559bbe6b9c5e5945f0ee31))

## [5.2.2](https://github.com/googlemaps/android-maps-ktx/compare/v5.2.1...v5.2.2) (2025-12-16)


### Bug Fixes

* Corrected broken links in Support section of README.md ([#355](https://github.com/googlemaps/android-maps-ktx/issues/355)) ([838913b](https://github.com/googlemaps/android-maps-ktx/commit/838913bc8d70cc7a3715e5f927837b9fa7ba3ae2))
