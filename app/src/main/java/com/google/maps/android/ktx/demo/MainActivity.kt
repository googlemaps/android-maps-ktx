package com.google.maps.android.ktx.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.ktx.MapsExperimentalFeature
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.demo.model.MyItem
import org.json.JSONException

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    var mIsRestore: Boolean = false

    @OptIn(MapsExperimentalFeature::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsRestore = savedInstanceState != null
        setContentView(R.layout.activity_main)
        lifecycle.coroutineScope.launchWhenCreated {
            val mapFragment : SupportMapFragment? =
                supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            check(mapFragment != null)
            val googleMap = mapFragment.awaitMap()
            showMapLayers(googleMap)
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        map?: return
        if (!mIsRestore) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.403186, -0.126446),10F))
        }
        showMapLayers(map)
    }

    private fun showMapLayers(map: GoogleMap) {

        // Shared object managers - used to support multiple layer types on the map simultaneously
        val markerManager = MarkerManager(map)
        val groundOverlayManager = GroundOverlayManager(map)
        val polygonManager = PolygonManager(map)
        val polylineManager = PolylineManager(map)

        // Add clustering
        val clusterManager = ClusterManager<MyItem>(this, map, markerManager)
        map.setOnCameraIdleListener(clusterManager)
        //addClusterItems(clusterManager)
    }

//    private fun addClusterItems(clusterManager: ClusterManager<*>) {
//        val inputStream = resources.openRawResource(R.raw.radar_search)
//        try {
//            val items = MyItemReader().read(inputStream)
//            clusterManager.addItems(items)
//        } catch (e: JSONException) {
//            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show()
//            e.printStackTrace()
//        }
//    }
}
