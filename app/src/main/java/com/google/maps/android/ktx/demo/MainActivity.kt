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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.Renderer.ImagesCache
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.demo.io.MyItemReader
import com.google.maps.android.ktx.demo.model.MyItem
import com.google.maps.android.ktx.utils.collection.addMarker
import com.google.maps.android.ktx.utils.geojson.geoJsonLayer
import com.google.maps.android.ktx.utils.kml.kmlLayer
import org.json.JSONException

/**
 * A demo of multiple layers on the map.
 *
 * To add your Maps API key to this project:
 *   1. Create a file app/secure.properties
 *   2. Add this line, where YOUR_API_KEY is your API key:
 *        MAPS_API_KEY=YOUR_API_KEY
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isRestore = savedInstanceState != null
        setContentView(R.layout.activity_main)

        if (getString(R.string.maps_api_key).isEmpty()) {
            Toast.makeText(this, "Add your own API key in app/secure.properties as MAPS_API_KEY=YOUR_API_KEY", Toast.LENGTH_LONG).show()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        lifecycle.coroutineScope.launchWhenCreated {
            val googleMap = mapFragment?.awaitMap()
            if (!isRestore) {
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(51.403186, -0.126446),
                        10F
                    )
                )
            }
            googleMap?.let { showMapLayers(it) }
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
            imagesCache = getImagesCache()
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
            imagesCache = getImagesCache() // Alternately, you could remove this line to not use the cache
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

    /**
     * Returns a cache that survives configuration changes
     */
    private fun getImagesCache(): ImagesCache? {
        val retainFragment = RetainFragment.findOrCreateRetainFragment(supportFragmentManager)
        return retainFragment.mImagesCache
    }
}
