# Gemini Code Assist Style Guide: android-maps-ktx

This guide defines the custom code review and generation rules for the `android-maps-ktx` and `android-maps-utils-ktx` repositories.

---

## 🚀 Kotlin Extensions (KTX) Idioms
- **Reactive Streams over Callbacks**: Prefer subscribing to reactive KTX Flows (e.g. `fineLocationEvents()`, `clusterClickEvents()`, `cameraIdleEvents()`) rather than overriding standard Maps SDK listeners or executing periodic polling tasks.
- **Lifecycle-Aware Scopes**: Always launch and collect Flow subscriptions inside lifecycle-aware scopes (such as `lifecycleScope` or `repeatOnLifecycle`) to prevent memory leaks and coordinate background subscription cancellations safely.

---

## ⚠️ Multi-Subscriber Multicasting Rules
- **Single-Listener Limitation**: The underlying Google Maps SDK only supports a single callback slot per event listener (e.g. one `setOnMarkerClickListener` or `setOnCameraIdleListener`).
- **The Hijacking Gotcha**: Because KTX click and camera flows are cold, having multiple concurrent collectors subscribe to them directly will override and hijack this single listener slot. Cancelling any single collector will completely clear the slot, silently breaking all remaining active streams!
- **multicasting Invariant**: If you need multiple concurrent observers to collect from the same click or camera stream, **always** multicast the cold flow into a hot `SharedFlow` using the `.shareIn()` operator:

```kotlin
val sharedClicks = markerCollection.clickEvents()
    .shareIn(
        scope = lifecycleScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 0
    )
```

---

## 🧪 Visual Regression & Test Coverage Guidelines
- **Automatic Coverage**: Any new KTX location or click extension must be fully covered by both unit tests and mock UI integration tests.
- **Golden Verification**: Support layout testing against visual goldens under `app/src/androidTest/assets/goldens/` utilizing multimodal AI comparison to safeguard map view integrity.
- **Clean Captures**: Run Clean SystemUI scripts and pre-configure test mock location providers in automated screen capture workflows to ensure consistent status bar headers and survive cold flow cancellations.
