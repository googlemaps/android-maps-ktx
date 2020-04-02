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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.Renderer.ImagesCache
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import com.google.maps.android.ktx.MapsExperimentalFeature
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.demo.io.MyItemReader
import com.google.maps.android.ktx.demo.model.MyItem
import com.google.maps.android.ktx.utils.geojson.geoJsonLayer
import com.google.maps.android.ktx.utils.kml.kmlLayer
import org.json.JSONException

/**
 * A demo of multiple layers on the map.
 *
 * To add your Maps API key to this project:
 *   1. Create a file app/gradle.properties
 *   2. Add this line, where YOUR_API_KEY is your API key:
 *        MAPS_API_KEY="YOUR_API_KEY"
 */
class MainActivity : AppCompatActivity() {
    var mIsRestore: Boolean = false

    @MapsExperimentalFeature
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsRestore = savedInstanceState != null
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        lifecycle.coroutineScope.launchWhenCreated {
            check(mapFragment != null)
            val googleMap = mapFragment.awaitMap()
            if (!mIsRestore) {
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(51.403186, -0.126446),
                        10F
                    )
                )
            }
            showMapLayers(googleMap)
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
        val geoJsonLineStringStyle = GeoJsonLineStringStyle()
        geoJsonLineStringStyle.color = Color.RED
        for (f in geoJsonLineLayer.features) {
            f.lineStringStyle = geoJsonLineStringStyle
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
        val geoJsonPolygonStyle = GeoJsonPolygonStyle()
        geoJsonPolygonStyle.fillColor = Color.RED
        for (f in geoJsonPolygonLayer.features) {
            f.polygonStyle = geoJsonPolygonStyle
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
        markerCollection.addMarker(
            MarkerOptions()
                .position(LatLng(51.150000, -0.150032))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Unclustered marker")
        )
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
