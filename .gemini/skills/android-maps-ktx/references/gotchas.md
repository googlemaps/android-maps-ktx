# Critical Gotchas & Stability Workarounds

This reference encodes the hidden gotchas, crash risks, and architectural constraints you must follow when developing with the Android Maps KTX library.

---

## ⚠️ Gotcha 1: The Single-Subscriber Invariant (Cold Flows)

### 🚫 The Problem
The underlying Google Maps SDK only supports a **single listener slot** per event type (e.g., only one `setOnMarkerClickListener` or `setOnCameraIdleListener` callback can be active at any time). 

Because the KTX Flows (`clusterItemClickEvents()`, `clickEvents()`, `cameraIdleEvents()`) are **cold flows**, they register a new listener callback on the SDK *every time a collector begins gathering updates*.
- If two active coroutines subscribe to the same cold click flow concurrently, the second subscriber will overwrite the listener slot, **hijacking** the stream. The first subscriber will silently stop receiving click events.
- Worse, if either subscriber is cancelled, the KTX flow's `awaitClose` block will run, completely clearing the listener slot on the Google Maps SDK (`setOnMarkerClickListener(null)`). This **silently kills** all remaining active collectors!

###  The Solution
If you have multiple active collectors subscribing to the same click, camera, or shape event stream, **always** share the cold flow by multicasting it into a hot `SharedFlow` using `.shareIn()`:

```kotlin
// Wrap the cold KTX flow into a Hot SharedFlow to support multiple concurrent collectors safely
val sharedMarkerClicks: SharedFlow<Marker> = markerCollection.clickEvents()
    .shareIn(
        scope = lifecycleScope,
        started = SharingStarted.WhileSubscribed(5000), // Keep upstream active 5s after last collector leaves
        replay = 0                                      // Do not replay past clicks to new subscribers
    )

// Observer 1 (e.g., logs to telemetry panel)
scope.launch {
    sharedMarkerClicks.collect { marker ->
        logView.append("Observer 1: Clicked '${marker.title}'")
    }
}

// Observer 2 (e.g., plays click sound or displays info bubble)
scope.launch {
    sharedMarkerClicks.collect { marker ->
        marker.showInfoWindow()
    }
}
```

---

## ⚠️ Gotcha 2: SystemUI Tuner Crashes (Android 15+ / SDK 37)

### 🚫 The Problem
When generating pixel-perfect catalog snapshots or videos, it is common to use the Android `SystemUI Tuner` via `adb shell am broadcast -a com.android.systemui.demo` (Demo Mode) to hide battery levels, wifi strengths, and network icons to keep status bars clean.

However, in **Android 15+ (SDK 37)**, attempting to disable or force wifi/mobile cell symbols via demo mode tuner broadcasts causes a fatal `DemoMobileConnectionsRepository` NullPointerException, causing the emulator's system UI server to crash repeatedly!

###  The Solution
When configuring Demo Mode on Android 15+ emulators, **never** broadcast wifi or mobile network override signals. Set a clean clock, full battery, and disable notification icons safely using these exact broadcasts:

```bash
# 1. Enable Demo Mode
adb shell settings put global sysui_demo_allowed 1

# 2. Set a clean clock (e.g., 12:00)
adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1200

# 3. Set battery to 100% (plugged in)
adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged true

# 4. Hide notification icons (clear status bar clutter)
adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false
```

---

## ⚠️ Gotcha 3: Mock Location Lifecycle (Cold Flow Cancellations)

### 🚫 The Problem
When testing location flows (`fineLocationEvents`, `coarseLocationEvents`) on-device, you must inject mock locations. 

If you start the `SnippetExecutionActivity` and launch the coroutines that subscribe to KTX location flows *before* creating or enabling the mock location providers in the adb terminal, the OS Location Services daemon will temporarily report `PROVIDER_DISABLED`.
This throws a `CancellationException` inside the KTX cold flow subscription, **immediately cancelling** the coroutine and closing the stream. Subsequent mock coordinate injections will be ignored completely!

###  The Solution
Always pre-configure and enable mock location providers in the shell **before** launching the target map Activity:

```bash
# 1. Grant mock location appops to both Shell and the Demo App
adb shell appops set com.android.shell android:mock_location allow
adb shell appops set com.google.maps.android.ktx.demo android:mock_location allow

# 2. Pre-create and enable the target mock provider (gps or network)
adb shell cmd location providers add-test-provider gps
adb shell cmd location providers set-test-provider-enabled gps true

# 3. NOW launch the Activity safely
adb shell "am start -W -n com.google.maps.android.ktx.demo/com.google.maps.android.ktx.demo.execution.SnippetExecutionActivity --es EXTRA_SNIPPET_TITLE \"Fine Location Flow\""

# 4. Inject locations safely
adb shell cmd location providers set-test-provider-location gps --location 51.5074,-0.1278
```
