/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.ktx.demo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.appbar.MaterialToolbar
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.Renderer.ImagesCache
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import com.google.maps.android.ktx.*
import com.google.maps.android.ktx.demo.io.MyItemReader
import com.google.maps.android.ktx.demo.model.CacheViewModel
import com.google.maps.android.ktx.demo.model.MyItem
import com.google.maps.android.ktx.model.cameraPosition
import com.google.maps.android.ktx.utils.collection.addMarker
import com.google.maps.android.ktx.utils.geojson.geoJsonLayer
import com.google.maps.android.ktx.utils.kml.kmlLayer
import kotlinx.coroutines.launch
import org.json.JSONException

/**
 * A demo of multiple layers on the map.
 *
 * To add your Maps API key to this project:
 *   1. Create a file ./secrets.properties
 *   2. Add this line, where YOUR_API_KEY is your API key:
 *        MAPS_API_KEY=YOUR_API_KEY
 */
class MainActivity : AppCompatActivity() {

    private val london = LatLng(51.403186, -0.126446)
    private val sanFrancisco = LatLng( 37.7576, -122.4194)
    private var currentLocation = london

    private val cacheViewModel: CacheViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val isRestore = savedInstanceState != null
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val container = findViewById<ConstraintLayout>(R.id.main_container)
        applyInsets(container)

        if (BuildConfig.MAPS_API_KEY.isEmpty()) {
            Toast.makeText(this, "Add your own API key in ./secrets.properties as MAPS_API_KEY=YOUR_API_KEY", Toast.LENGTH_LONG).show()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                val googleMap = mapFragment.awaitMap()
                if (!isRestore) {
                    googleMap.awaitMapLoad()
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            london,
                            10F
                        )
                    )
                }
                showMapLayers(googleMap)
                addButtonClickListener(googleMap)
                launch {
                    googleMap.cameraMoveStartedEvents().collect {
                        Log.d(TAG, "Camera moved - reason $it")
                    }
                }
                launch {
                    googleMap.cameraIdleEvents().collect {
                        Log.d(TAG, "Camera is idle.")
                    }
                }
            }
        }
    }

    private fun addButtonClickListener(googleMap: GoogleMap) {
        findViewById<Button>(R.id.button_animate_camera).setOnClickListener {
            currentLocation = if (currentLocation == london) sanFrancisco else london
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    googleMap.run {
                        awaitAnimateCamera(CameraUpdateFactory.newCameraPosition(
                            cameraPosition {
                                target(currentLocation)
                                zoom(0.0f)
                                tilt(0.0f)
                                bearing(0.0f)
                            }
                        ))
                        awaitMapLoad()
                        awaitAnimateCamera(CameraUpdateFactory.newCameraPosition(
                            cameraPosition {
                                target(currentLocation)
                                zoom(10.0f)
                                bearing(180f)
                                tilt(75f)
                                build()
                            }
                        ))
                    }
                }
            }
        }

        findViewById<Button>(R.id.button_snapshot).setOnClickListener {
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val bitmap = googleMap.awaitSnapshot()
                    findViewById<ImageView>(R.id.image_view_snapshot).setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun showMapLayers(map: GoogleMap) {
        // Shared object managers - used to support multiple layer types on the map simultaneously
        val markerManager = MarkerManager(map)
        val groundOverlayManager = GroundOverlayManager(map)
        val polygonManager = PolygonManager(map)
        val polylineManager = PolylineManager(map)

        addClusters(map, markerManager)
        addGeoJson(map, markerManager, polylineManager, polygonManager, groundOverlayManager)
        addKml(map, markerManager, polylineManager, polygonManager, groundOverlayManager)
        addMarker(markerManager)
    }

    private fun addClusters(map: GoogleMap, markerManager: MarkerManager) {
        val clusterManager = ClusterManager<MyItem>(this, map, markerManager)
        map.setOnCameraIdleListener(clusterManager)

        try {
            val items = MyItemReader().read(resources.openRawResource(R.raw.radar_search))
            clusterManager.addItems(items)
        } catch (e: JSONException) {
            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun addGeoJson(
        map: GoogleMap,
        markerManager: MarkerManager,
        polylineManager: PolylineManager,
        polygonManager: PolygonManager,
        groundOverlayManager: GroundOverlayManager
    ) {
        // GeoJSON polyline
        val geoJsonLineLayer = geoJsonLayer(
            map = map,
            resourceId = R.raw.south_london_line_geojson,
            context = this,
            markerManager = markerManager,
            polygonManager = polygonManager,
            polylineManager = polylineManager,
            groundOverlayManager = groundOverlayManager
        )
        // Make the line red
        geoJsonLineLayer.features.forEach {
            it.lineStringStyle = GeoJsonLineStringStyle().apply {
                color = Color.RED
            }
        }

        geoJsonLineLayer.addLayerToMap()
        geoJsonLineLayer.setOnFeatureClickListener { feature ->
            Toast.makeText(
                this,
                "GeoJSON polyline clicked: " + feature.getProperty("title"),
                Toast.LENGTH_SHORT
            ).show()
        }

        // GeoJSON polygon
        val geoJsonPolygonLayer = geoJsonLayer(
            map = map,
            resourceId = R.raw.south_london_square_geojson,
            context = this,
            markerManager = markerManager,
            polygonManager = polygonManager,
            polylineManager = polylineManager,
            groundOverlayManager = groundOverlayManager
        )
        // Fill it with red
        geoJsonPolygonLayer.features.forEach {
            it.polygonStyle = GeoJsonPolygonStyle().apply {
                fillColor = Color.RED
            }
        }

        geoJsonPolygonLayer.addLayerToMap()
        geoJsonPolygonLayer.setOnFeatureClickListener { feature ->
            Toast.makeText(
                this,
                "GeoJSON polygon clicked: " + feature.getProperty("title"),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addKml(
        map: GoogleMap,
        markerManager: MarkerManager,
        polylineManager: PolylineManager,
        polygonManager: PolygonManager,
        groundOverlayManager: GroundOverlayManager
    ) {
        // KML Polyline
        val kmlPolylineLayer = kmlLayer(
            map = map,
            resourceId = R.raw.south_london_line_kml,
            context = this,
            markerManager = markerManager,
            polygonManager = polygonManager,
            polylineManager = polylineManager,
            groundOverlayManager = groundOverlayManager,
            imagesCache = cacheViewModel.imagesCache
        )
        kmlPolylineLayer.addLayerToMap()
        kmlPolylineLayer.setOnFeatureClickListener { feature ->
            Toast.makeText(
                this,
                "KML polyline clicked: " + feature.getProperty("name"),
                Toast.LENGTH_SHORT
            ).show()
        }

        // KML Polygon
        val kmlPolygonLayer = kmlLayer(
            map = map,
            resourceId = R.raw.south_london_square_kml,
            context = this,
            markerManager = markerManager,
            polygonManager = polygonManager,
            polylineManager = polylineManager,
            groundOverlayManager = groundOverlayManager,
            imagesCache = cacheViewModel.imagesCache // Alternately, you could remove this line to not use the cache
        )
        kmlPolygonLayer.addLayerToMap()
        kmlPolygonLayer.setOnFeatureClickListener { feature ->
            Toast.makeText(
                this,
                "KML polygon clicked: " + feature.getProperty("name"),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addMarker(markerManager: MarkerManager) {
        // Unclustered marker - instead of adding to the map directly, use the MarkerManager
        val markerCollection: MarkerManager.Collection = markerManager.newCollection()
        markerCollection.addMarker {
            position(LatLng(51.150000, -0.150032))
            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            title("Unclustered marker")
        }
        markerCollection.setOnMarkerClickListener { marker ->
            Toast.makeText(
                this,
                "Marker clicked: " + marker.title,
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        fun applyInsets(container: View) {
            ViewCompat.setOnApplyWindowInsetsListener(
                container
            ) { view, insets ->
                val innerPadding =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
                view.setPadding(innerPadding.left, innerPadding.top, innerPadding.right, innerPadding.bottom)
                insets
            }
        }
    }
}