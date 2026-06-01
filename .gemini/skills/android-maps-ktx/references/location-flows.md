# Reactive Location Streaming Flows

This reference provides the implementation patterns and testing commands for consuming satellite location streams reactively as cold Flows via `maps-utils-ktx`.

---

## 📋 Permissions Requirements

Location streaming relies on standard Android hardware permissions. Declare them in your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## 🚀 Kotlin KTX Flow Implementations

The KTX location extension converts the legacy `LocationListener` callback pattern into clean, lifecycle-aware cold Flows that start and stop location updates automatically as collectors subscribe and unsubscribe.

### 1. Fine Location GPS Flow
Collects coordinate updates from `LocationManager.GPS_PROVIDER`. Requires `ACCESS_FINE_LOCATION`.

```kotlin
import com.google.maps.android.ktx.utils.location.fineLocationEvents

@SuppressLint("MissingPermission")
fun subscribeToFineGPS(
    locationManager: LocationManager,
    scope: CoroutineScope,
    onLocationReceived: (Location) -> Unit,
    onGPSDisabled: () -> Unit
) {
    scope.launch {
        try {
            // Collect updates with minTime=2s and minDistance=1m
            locationManager.fineLocationEvents(minTimeMs = 2000L, minDistanceM = 1f)
                .collect { location ->
                    onLocationReceived(location)
                }
        } catch (e: CancellationException) {
            // Triggers immediately if GPS gets disabled in settings mid-stream
            onGPSDisabled()
        }
    }
}
```

### 2. Coarse Location Network Flow
Collects wifi and cell tower location updates from `LocationManager.NETWORK_PROVIDER`. Requires `ACCESS_COARSE_LOCATION`.

```kotlin
import com.google.maps.android.ktx.utils.location.coarseLocationEvents

@SuppressLint("MissingPermission")
fun subscribeToCoarseNetwork(
    locationManager: LocationManager,
    scope: CoroutineScope,
    onLocationReceived: (Location) -> Unit,
    onNetworkDisabled: () -> Unit
) {
    scope.launch {
        try {
            // Collect updates with minTime=5s and minDistance=5m
            locationManager.coarseLocationEvents(minTimeMs = 5000L, minDistanceM = 5f)
                .collect { location ->
                    onLocationReceived(location)
                }
        } catch (e: CancellationException) {
            onNetworkDisabled()
        }
    }
}
```

---

## 🧪 Automated testing & mock coordinate injection

To verify location stream flows on an emulator, you can use the Android location subsystem test provider commands inside `adb`.

### London Fine GPS Trajectory Simulation
Simulates a fine GPS device location sweep in London, plotting successive marker pins on the map:

```bash
# 1. Pre-configure test provider before launching
adb shell appops set com.google.maps.android.ktx.demo android:mock_location allow
adb shell cmd location providers add-test-provider gps
adb shell cmd location providers set-test-provider-enabled gps true

# 2. Launch Fine Location snippet activity
adb shell "am start -W -n com.google.maps.android.ktx.demo/com.google.maps.android.ktx.demo.execution.SnippetExecutionActivity --es EXTRA_SNIPPET_TITLE \"Fine Location Flow\""
sleep 3

# 3. Inject successive coordinates with fresh device timestamps
# Point 1: London Center
TIME=$(adb shell date +%s%3N)
adb shell cmd location providers set-test-provider-location gps --location 51.5074,-0.1278 --time $TIME
sleep 2.5

# Point 2: London South
TIME=$(adb shell date +%s%3N)
adb shell cmd location providers set-test-provider-location gps --location 51.4800,-0.1200 --time $TIME
sleep 2.5

# Point 3: London East
TIME=$(adb shell date +%s%3N)
adb shell cmd location providers set-test-provider-location gps --location 51.4950,-0.0800 --time $TIME
sleep 3.5

# 4. Clean up test provider
adb shell cmd location providers set-test-provider-enabled gps false
adb shell cmd location providers remove-test-provider gps
```

### Paris Coarse Network Trajectory Simulation
Simulates a coarse network location sweep in Paris:

```bash
# 1. Pre-configure test provider before launching
adb shell appops set com.google.maps.android.ktx.demo android:mock_location allow
adb shell cmd location providers add-test-provider network
adb shell cmd location providers set-test-provider-enabled network true

# 2. Launch Coarse Location snippet activity
adb shell "am start -W -n com.google.maps.android.ktx.demo/com.google.maps.android.ktx.demo.execution.SnippetExecutionActivity --es EXTRA_SNIPPET_TITLE \"Coarse Location Flow\""
sleep 3

# 3. Inject successive network coordinates
# Point 1: Paris Center
TIME=$(adb shell date +%s%3N)
adb shell cmd location providers set-test-provider-location network --location 48.8566,2.3522 --time $TIME
sleep 5.5

# Point 2: Paris South
TIME=$(adb shell date +%s%3N)
adb shell cmd location providers set-test-provider-location network --location 48.8300,2.3400 --time $TIME
sleep 5.5

# Point 3: Paris East
TIME=$(adb shell date +%s%3N)
adb shell cmd location providers set-test-provider-location network --location 48.8400,2.3800 --time $TIME
sleep 6.5

# 4. Clean up test provider
adb shell cmd location providers set-test-provider-enabled network false
adb shell cmd location providers remove-test-provider network
```
