/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.ktx.demo.execution

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.CircleManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.awaitAnimateCamera
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.cameraIdleEvents
import com.google.maps.android.ktx.demo.R
import com.google.maps.android.ktx.demo.snippets.ClusteringSnippets
import com.google.maps.android.ktx.demo.snippets.CollectionSnippets
import com.google.maps.android.ktx.demo.snippets.ExistingKtxSnippets
import com.google.maps.android.ktx.demo.snippets.LocationSnippets
import com.google.maps.android.ktx.utils.geojson.geoJsonLayer
import com.google.maps.android.ktx.utils.location.fineLocationEvents
import com.google.maps.android.ktx.utils.location.coarseLocationEvents
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.launch

class SnippetExecutionActivity : AppCompatActivity() {

    private val london = LatLng(51.403186, -0.126446)
    private var googleMap: GoogleMap? = null
    private var userLocationMarker: Marker? = null
    private val TAG = "SnippetExecution"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Request edge-to-edge window decor layout
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_snippet_execution)

        // 2. Apply top system insets + cutout safe padding to the Execution Toolbar
        val appBarLayout: View = findViewById(R.id.execution_appbar)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { view, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(
                view.paddingLeft,
                systemBarInsets.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        // 3. Apply bottom insets to the execution float action button to prevent navigation bar overlapping
        val btnExecuteCamera: Button = findViewById(R.id.btn_execute_camera)
        ViewCompat.setOnApplyWindowInsetsListener(btnExecuteCamera) { view, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val params = view.layoutParams as android.view.ViewGroup.MarginLayoutParams
            params.bottomMargin = systemBarInsets.bottom + 16.dpToPx(this@SnippetExecutionActivity)
            view.layoutParams = params
            insets
        }

        val snippetTitle = intent.getStringExtra("EXTRA_SNIPPET_TITLE") ?: "Map Initialization"
        supportActionBar?.title = snippetTitle
        findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.execution_toolbar).title = snippetTitle

        val mapFragment = supportFragmentManager.findFragmentById(R.id.execution_map) as SupportMapFragment

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                val map = mapFragment.awaitMap()
                googleMap = map
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 10f))

                // Route snippet title to its respective execution flow!
                when (snippetTitle) {
                    "Map Initialization" -> {
                        // Execute existing API snippet: awaitMap and load styled GeoJSON layers
                        ExistingKtxSnippets.buildGeoJsonLayerSnippet(map, this@SnippetExecutionActivity, R.raw.south_london_square_geojson).apply {
                            addLayerToMap()
                        }
                    }
                    "Animate Camera (Coroutines)" -> {
                        // Setup baseline overlays
                        ExistingKtxSnippets.buildGeoJsonLayerSnippet(map, this@SnippetExecutionActivity, R.raw.south_london_square_geojson).apply {
                            addLayerToMap()
                        }
                        
                        val panel: View = findViewById(R.id.panel_telemetry)
                        val logView: android.widget.TextView = findViewById(R.id.text_telemetry_log)
                        panel.visibility = View.VISIBLE
                        findViewById<android.widget.TextView>(R.id.text_telemetry_status).text = "STATUS: KTX COROUTINE ACTIVE"

                        logBuffer.setLength(0)
                        logBuffer.append("[12:00:00] Ready for camera animation. Click button below.\n")
                        logView.text = logBuffer.toString()

                        btnExecuteCamera.visibility = View.VISIBLE
                        btnExecuteCamera.setOnClickListener {
                            logBuffer.append("[12:00:01] Triggering awaitAnimateCamera()...\n")
                            logView.text = logBuffer.toString()
                            
                            ExistingKtxSnippets.awaitCameraAnimationSnippet(map, lifecycleScope, LatLng(51.5074, -0.1278)) {
                                Log.d(TAG, "Animate Camera KTX Coroutine Completed!")
                                val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                                logBuffer.append("[$time] ANIMATION SUSPEND COMPLETED! Coroutine resumed.\n")
                                logView.text = logBuffer.toString()
                            }
                        }
                    }
                    "Camera Idle Events Flow" -> {
                        val panel: View = findViewById(R.id.panel_telemetry)
                        val logView: android.widget.TextView = findViewById(R.id.text_telemetry_log)
                        panel.visibility = View.VISIBLE
                        findViewById<android.widget.TextView>(R.id.text_telemetry_status).text = "STATUS: CAMERA IDLE FLOW ACTIVE"

                        logBuffer.setLength(0)
                        logBuffer.append("[12:00:00] CameraIdle Flow subbed.\n")
                        logView.text = logBuffer.toString()

                        ExistingKtxSnippets.cameraIdleEventsFlowSnippet(map, lifecycleScope) {
                            val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                            val target = map.cameraPosition.target
                            logBuffer.append("[$time] CAMERA IDLE: LatLng: (${String.format("%.4f", target.latitude)}, ${String.format("%.4f", target.longitude)}), Zoom: ${String.format("%.1f", map.cameraPosition.zoom)}\n")
                            logView.text = logBuffer.toString()
                        }
                    }
                    "Fine Location Flow" -> {
                        startFineLocationStream()
                    }
                    "Coarse Location Flow" -> {
                        startCoarseLocationStream()
                    }
                    "Marker Cluster Click Flow" -> {
                        setupClusteringDemo(map)
                    }
                    "Marker Collection Click Flow" -> {
                        setupCollectionDemo(map)
                    }
                }
            }
        }
    }

    private var routePoints = mutableListOf<LatLng>()
    private var routePolyline: com.google.android.gms.maps.model.Polyline? = null
    private var logBuffer = StringBuilder()

    @SuppressLint("MissingPermission")
    private fun startFineLocationStream() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val panel: View = findViewById(R.id.panel_telemetry)
        val logView: android.widget.TextView = findViewById(R.id.text_telemetry_log)
        panel.visibility = View.VISIBLE
        findViewById<android.widget.TextView>(R.id.text_telemetry_status).text = "STATUS: KTX FLOW ACTIVE STREAMING (FINE)"

        logBuffer.append("[12:00:00] FineLocation Flow subbed.\n")
        logView.text = logBuffer.toString()

        LocationSnippets.fineLocationFlowSnippet(
            locationManager,
            scope = lifecycleScope,
            onLocationReceived = { location ->
                val pos = LatLng(location.latitude, location.longitude)
                Log.d(TAG, "Location streamed: ${location.latitude}, ${location.longitude}")
                
                // Append dynamic telemetry log
                val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(location.time))
                logBuffer.append("[$time] LatLng: (${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)})\n")
                logView.text = logBuffer.toString()

                drawUserLocationMarker(pos)
                plotRoutePath(pos)
            },
            onGPSDisabled = {
                Toast.makeText(this, "GPS Disabled!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    @SuppressLint("MissingPermission")
    private fun startCoarseLocationStream() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val panel: View = findViewById(R.id.panel_telemetry)
        val logView: android.widget.TextView = findViewById(R.id.text_telemetry_log)
        panel.visibility = View.VISIBLE
        findViewById<android.widget.TextView>(R.id.text_telemetry_status).text = "STATUS: KTX FLOW ACTIVE STREAMING (COARSE)"

        logBuffer.append("[12:00:00] CoarseLocation Flow subbed.\n")
        logView.text = logBuffer.toString()

        LocationSnippets.coarseLocationFlowSnippet(
            locationManager,
            scope = lifecycleScope,
            onLocationReceived = { location ->
                val pos = LatLng(location.latitude, location.longitude)
                Log.d(TAG, "Coarse Location streamed: ${location.latitude}, ${location.longitude}")
                
                val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(location.time))
                logBuffer.append("[$time] Coarse: (${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)})\n")
                logView.text = logBuffer.toString()

                drawUserLocationMarker(pos)
                plotRoutePath(pos)
            },
            onGPSDisabled = {
                Toast.makeText(this, "Network Provider Disabled!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun drawUserLocationMarker(position: LatLng) {
        val map = googleMap ?: return
        // We retain all sequence pins to visually convey consecutive coordinate changes!
        map.addMarker(
            MarkerOptions()
                .position(position)
                .title("User Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12f))
    }

    private fun plotRoutePath(position: LatLng) {
        val map = googleMap ?: return
        routePoints.add(position)
        
        routePolyline?.remove()
        routePolyline = map.addPolyline(
            com.google.android.gms.maps.model.PolylineOptions()
                .addAll(routePoints)
                .color(android.graphics.Color.parseColor("#1A73E8"))
                .width(8f)
                .pattern(listOf(com.google.android.gms.maps.model.Dot(), com.google.android.gms.maps.model.Gap(10f)))
        )
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun setupClusteringDemo(map: GoogleMap) {
        val panel: View = findViewById(R.id.panel_telemetry)
        val logView: android.widget.TextView = findViewById(R.id.text_telemetry_log)
        panel.visibility = View.VISIBLE
        findViewById<android.widget.TextView>(R.id.text_telemetry_status).text = "STATUS: KTX CLUSTER FLOWS ACTIVE"

        logBuffer.setLength(0)
        logBuffer.append("[12:00:00] Clustering Click Flows subbed.\n")
        logView.text = logBuffer.toString()

        val clusterManager = ClusterManager<com.google.maps.android.ktx.demo.model.MyItem>(this, map)
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        // Add mock cluster items from radar search resource
        val inputStream = resources.openRawResource(R.raw.radar_search)
        val mockItems = com.google.maps.android.ktx.demo.io.MyItemReader().read(inputStream)
        clusterManager.addItems(mockItems)
        clusterManager.cluster()

        // 1. Cluster Click Flow
        ClusteringSnippets.clusterClicksSnippet(
            clusterManager,
            scope = lifecycleScope,
            onClusterClicked = { cluster ->
                val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                logBuffer.append("[$time] CLUSTER CLICKED: Size ${cluster.size} at (${String.format("%.4f", cluster.position.latitude)}, ${String.format("%.4f", cluster.position.longitude)})\n")
                logView.text = logBuffer.toString()

                lifecycleScope.launch {
                    map.awaitAnimateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            cluster.position,
                            map.cameraPosition.zoom + 2f
                        )
                    )
                }
            }
        )

        // 2. Cluster Item Click Flow (Demonstrating multicast SharedFlow observers)
        ClusteringSnippets.clusterItemClicksSnippet(
            clusterManager,
            scope = lifecycleScope,
            onObserverOneReceived = { item ->
                val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                logBuffer.append("[$time] OBSERVER 1: Clicked item '${item.title}'\n")
                logView.text = logBuffer.toString()
            },
            onObserverTwoReceived = { item ->
                val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                logBuffer.append("[$time] OBSERVER 2: Clicked item '${item.title}'\n")
                logView.text = logBuffer.toString()
            }
        )
    }

    private fun setupCollectionDemo(map: GoogleMap) {
        val panel: View = findViewById(R.id.panel_telemetry)
        val logView: android.widget.TextView = findViewById(R.id.text_telemetry_log)
        panel.visibility = View.VISIBLE
        findViewById<android.widget.TextView>(R.id.text_telemetry_status).text = "STATUS: KTX COLLECTION FLOWS ACTIVE"

        logBuffer.setLength(0)
        logBuffer.append("[12:00:00] Collection Click Flows subbed.\n")
        logView.text = logBuffer.toString()

        val centerPos = LatLng(51.150000, -0.150032)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPos, 13f))

        // 1. Marker Collection Demo
        val markerManager = MarkerManager(map)
        val markerCollection = markerManager.newCollection()
        val markerItem = markerCollection.addMarker(
            MarkerOptions()
                .position(LatLng(51.150000, -0.152000))
                .title("Unclustered Marker")
                .snippet("Click to trigger KTX Flow")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        CollectionSnippets.markerCollectionClicksSnippet(
            markerCollection,
            scope = lifecycleScope,
            onMarkerClicked = { marker ->
                Log.d(TAG, "Collection Marker Click Flow: ${marker.title}")
                marker.showInfoWindow()
                
                val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                logBuffer.append("[$time] MARKER CLICKED: '${marker.title}'\n")
                logView.text = logBuffer.toString()
            }
        )

        // 2. Circle Collection Demo
        val circleManager = CircleManager(map)
        val circleCollection = circleManager.newCollection()
        val circle = circleCollection.addCircle(
            com.google.android.gms.maps.model.CircleOptions()
                .center(LatLng(51.154000, -0.148000))
                .radius(400.0)
                .clickable(true)
                .fillColor(android.graphics.Color.parseColor("#3F1A73E8"))
                .strokeColor(android.graphics.Color.parseColor("#1A73E8"))
                .strokeWidth(4f)
        )

        CollectionSnippets.circleCollectionClicksSnippet(
            circleCollection,
            scope = lifecycleScope,
            onCircleClicked = { clickedCircle ->
                Log.d(TAG, "Collection Circle Click Flow!")
                // Change styling as visual feedback of click success
                clickedCircle.fillColor = android.graphics.Color.parseColor("#3FDF3A30")
                clickedCircle.strokeColor = android.graphics.Color.parseColor("#DF3A30")
                
                val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                logBuffer.append("[$time] CIRCLE CLICKED -> Color updated to RED!\n")
                logView.text = logBuffer.toString()
            }
        )
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}
