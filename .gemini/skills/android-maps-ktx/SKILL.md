---
name: android-maps-ktx
description: >-
  Provides idiomatic Kotlin extension (KTX) patterns, reactive Flow event streams,
  and multi-subscriber shareIn rules for Google Maps SDK for Android and its
  Utility Library. Use when writing or refactoring Android map features with
  coroutines/flows, streaming GPS location updates, handling marker cluster clicks,
  managing styled overlay collections, or simulating adb coordinates. Don't use for
  vanilla Java Google Maps development or web/iOS maps APIs.
---

# Android Maps KTX Agent Skill

This skill provides verified patterns, gotchas, and reference implementations for developing with the **Android Maps KTX** and **Android Maps Utility KTX** libraries (`maps-ktx` and `maps-utils-ktx`).

It guides you in writing concise, idiomatic Kotlin using coroutines and Flows to manage map lifecycles, viewport transitions, satellite location streams, marker cluster events, and custom styled overlay click collections.

---

## 📋 Triggers & Scenarios

### Use when:
- Implementing or refactoring Android map interfaces using **Kotlin coroutines** or **Kotlin Flows**.
- Consuming reactive satellite location updates (`LocationManager`) with automatic subscriber lifecycles.
- Listening to marker group click events or individual pin clicks using `ClusterManager` event streams.
- Managing click callbacks across distinct, styled sub-collections using `MarkerManager` or `CircleManager`.
- Creating pixel-perfect visual previews or automated `.gif` animations of map interactions using `adb` shell inputs.

### Don't use for:
- Standard Java-based Google Maps SDK for Android development.
- Web Map developments (Maps JavaScript API) or iOS Maps integrations.
- General Android location services unrelated to Google Maps rendering or KTX.

---

## 🎯 Idiomatic KTX Core Capabilities

The Android Maps KTX libraries replace legacy callback-heavy interfaces with clean Kotlin-first abstractions. Refer to the following deep guides for implementations:

### 1. Critical Gotchas & Stability Workarounds
- **The Single-Subscriber Invariant**: Learn why multiple direct subscribers to a KTX cold flow will hijack the listener slot or break on cancellation, and how to safely multicast using `.shareIn()`.
- **SystemUI Tuner Stability**: Learn how to set clean status bars in Android 15+ (SDK 37) without triggering fatal tuner repository crashes.
- **Mock Location Lifecycle**: Understand why mock providers must be created *before* activity subscription.
- 📂 **[Gotchas & Stability Reference](references/gotchas.md)**

### 2. Reactive Location Streaming
- Cold flows for fine GPS location tracking and coarse cell/wifi tower tracking.
- Auto-cleanup of test location providers and lifecycle-safe subscriptions.
- adb coordinates injection for London (Fine GPS) and Paris (Coarse network) trajectories.
- 📂 **[Location Streams Reference](references/location-flows.md)**

### 3. Marker Clustering Click Streams
- Subscribing to group cluster click viewport glides via `clusterClickEvents()`.
- Multicasting individual marker clicks safely across dual observers via `clusterItemClickEvents()` and `SharedFlow`.
- adb touch badges simulations.
- 📂 **[Clustering click flows Reference](references/clustering-flows.md)**

### 4. Shape & Overlay Click Collections
- Propagating click events in clean, isolated sub-collections via KTX collection click extensions.
- Visual click confirmation rules (Persistent InfoWindows, interactive circle color-swapping on-device).
- 📂 **[Collection Click Flows Reference](references/collection-flows.md)**
