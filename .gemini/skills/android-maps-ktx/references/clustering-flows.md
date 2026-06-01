# Marker Clustering Click Event Flows

This reference provides implementation patterns and simulation inputs for consuming clustered marker click events via `maps-utils-ktx` Flows.

---

## 🚀 Kotlin KTX Flow Implementations

The `ClusterManager` click extensions convert standard callback listeners (`setOnClusterClickListener`, `setOnClusterItemClickListener`) into clean, reactive Flows. 

### Complete Multi-Observer Multicast Click Flow Pattern
This pattern shows how to safely subscribe to group cluster click events (performing map zoom-ins) and multicast individual item click events concurrently across multiple observers without slot hijacking.

```kotlin
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.utils.clustering.clusterClickEvents
import com.google.maps.android.ktx.utils.clustering.clusterItemClickEvents

fun setupClusteringFlows(
    map: GoogleMap,
    clusterManager: ClusterManager<MyItem>,
    scope: CoroutineScope,
    logView: TextView
) {
    map.setOnCameraIdleListener(clusterManager)
    map.setOnMarkerClickListener(clusterManager)

    // 1. Group Cluster Click Flow (Single collector)
    scope.launch {
        clusterManager.clusterClickEvents().collect { cluster ->
            // Zoom viewport directly onto clicked cluster coordinate KTX-style!
            map.awaitAnimateCamera(
                CameraUpdateFactory.newLatLngZoom(cluster.position, map.cameraPosition.zoom + 2f)
            )
            logView.append("Cluster Clicked! Contains ${cluster.size} items.")
        }
    }

    // 2. Individual Cluster Item Click Flow (Multicast SharedFlow observer pattern)
    val sharedItemClicks: SharedFlow<MyItem> = clusterManager.clusterItemClickEvents()
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            replay = 0
        )

    // Observer 1
    scope.launch {
        sharedItemClicks.collect { item ->
            logView.append("Observer 1 collected item: '${item.title}'")
        }
    }

    // Observer 2
    scope.launch {
        sharedItemClicks.collect { item ->
            logView.append("Observer 2 collected item: '${item.title}'")
        }
    }
}
```

---

## 🧪 On-Device Click Simulation & Automation

To automate marker cluster clicks on an emulator (e.g., for automated visual tests, screen recordings, or GIF generation), you can inject precise touch events using `adb shell input tap`.

### Automated Click Sweep Sequence
Taps the cluster badge at the center of the screen to zoom into individual pins, and then executes a robust sweep in the center region to guarantee clicking one of the expanded pins:

```bash
# 1. Launch Marker Cluster Click Flow view
adb shell "am start -W -n com.google.maps.android.ktx.demo/com.google.maps.android.ktx.demo.execution.SnippetExecutionActivity --es EXTRA_SNIPPET_TITLE \"Marker Cluster Click Flow\""
sleep 4 # Wait for base map tiles and clusters to fully render

# 2. Tap the main Cluster Badge near map center to trigger clusterClicksSnippet
echo "Tapping Cluster Badge at map center..."
adb shell input tap 540 1200
sleep 3 # Wait for KTX awaitAnimateCamera zoom transition to settle

# 3. Sweep-tap around center coordinates to hit one of the newly expanded individual markers
echo "Simulating sweep-tap to trigger clusterItemClicksSnippet on an individual marker..."
for offset in 0 -60 60; do
  for yoffset in 0 -60 60; do
    adb shell input tap $((540 + offset)) $((1200 + yoffset))
    sleep 0.4
  done
done
sleep 2
```
