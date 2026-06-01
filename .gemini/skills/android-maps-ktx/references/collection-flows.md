# Styled Shape & Overlay Click Flows

This reference provides implementation patterns and on-device simulation coordinates for styled collection click handlers via `maps-utils-ktx` Flows.

---

## 🚀 Kotlin KTX Flow Implementations

The Maps Utility Library provides `MarkerManager` and `CircleManager` to manage styled collections of overlays. KTX extensions provide clean `.clickEvents()` Flow abstractions over these collections.

### Dual Collection Click Flows Example
This example demonstrates how to listen to clicks on custom markers (displaying interactive InfoWindows) and clickable circles (updating shape colors in real-time to visually confirm successful clicks).

```kotlin
import com.google.maps.android.collections.CircleManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.utils.collection.clickEvents

fun setupCollectionFlows(
    map: GoogleMap,
    scope: CoroutineScope,
    logView: TextView
) {
    val centerPos = LatLng(51.150000, -0.150032)
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPos, 13f))

    // 1. Custom Marker Collection
    val markerManager = MarkerManager(map)
    val markerCollection = markerManager.newCollection()
    val marker = markerCollection.addMarker(
        MarkerOptions()
            .position(LatLng(51.150000, -0.152000))
            .title("Unclustered Marker")
            .snippet("Click to trigger KTX Flow")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
    )

    scope.launch {
        markerCollection.clickEvents().collect { clickedMarker ->
            // Display standard InfoWindow above the clicked marker
            clickedMarker.showInfoWindow()
            logView.append("Marker Clicked via KTX Flow: '${clickedMarker.title}'")
        }
    }

    // 2. Custom Circle Collection
    val circleManager = CircleManager(map)
    val circleCollection = circleManager.newCollection()
    val circle = circleCollection.addCircle(
        CircleOptions()
            .center(LatLng(51.154000, -0.148000))
            .radius(400.0)
            .clickable(true)
            .fillColor(android.graphics.Color.parseColor("#3F1A73E8")) // Light Blue
            .strokeColor(android.graphics.Color.parseColor("#1A73E8"))
            .strokeWidth(4f)
    )

    scope.launch {
        circleCollection.clickEvents().collect { clickedCircle ->
            // Change styling in real-time to provide persistent, clear visual feedback
            clickedCircle.fillColor = android.graphics.Color.parseColor("#3FDF3A30") // Soft Red
            clickedCircle.strokeColor = android.graphics.Color.parseColor("#DF3A30")
            logView.append("Circle Clicked! Styling updated to RED.")
        }
    }
}
```

---

## 🧪 On-Device Click Simulation & Automation

To automate overlay clicks on an emulator, you can inject precise touch events using `adb shell input tap`.

### Automated Click Sequence
Taps the custom Azure marker at its map coordinate, and then executes a robust sweep around the clickable circle's coordinates:

```bash
# 1. Launch Marker Collection Click Flow view
adb shell "am start -W -n com.google.maps.android.ktx.demo/com.google.maps.android.ktx.demo.execution.SnippetExecutionActivity --es EXTRA_SNIPPET_TITLE \"Marker Collection Click Flow\""
sleep 4 # Wait for map and overlays to render

# 2. Tap the Custom Azure Marker to trigger marker click flow
echo "Tapping Custom Azure Marker..."
adb shell input tap 540 1650
sleep 2.5 # Wait for InfoWindow to render

# 3. Sweep-tap around the clickable circle's coordinates to trigger circle click flow
echo "Simulating sweep-tap to click Custom Circle..."
for offset in 0 -50 50; do
  for yoffset in 0 -50 50; do
    adb shell input tap $((600 + offset)) $((1300 + yoffset))
    sleep 0.4
  done
done
sleep 2
```
