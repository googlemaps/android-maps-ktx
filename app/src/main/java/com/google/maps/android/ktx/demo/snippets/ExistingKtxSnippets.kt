/*
 * Copyright 2026 Google Inc.
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
 *
 */

package com.google.maps.android.ktx.demo.snippets

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.ktx.awaitAnimateCamera
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.cameraIdleEvents
import com.google.maps.android.ktx.utils.contains
import com.google.maps.android.ktx.utils.geojson.geoJsonLayer
import com.google.maps.android.ktx.utils.kml.kmlLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Standalone snippets demonstrating major existing KTX coroutine and spatial utility APIs in maps-ktx.
 */
public object ExistingKtxSnippets {

    /**
     * Demonstrates retrieving GoogleMap instance via suspend coroutine.
     */
    public fun awaitMapInitSnippet(
        mapFragment: SupportMapFragment,
        scope: CoroutineScope,
        onMapReady: (GoogleMap) -> Unit
    ) {
        // [START maps_android_ktx_coroutines_init]
        scope.launch {
            // Retrieves the GoogleMap instance non-blockingly!
            val googleMap = mapFragment.awaitMap()
            onMapReady(googleMap)
        }
        // [END maps_android_ktx_coroutines_init]
    }

    /**
     * Demonstrates smoothly animating the map camera and resuming execution ONLY
     * after the camera animation fully completes.
     */
    public fun awaitCameraAnimationSnippet(
        googleMap: GoogleMap,
        scope: CoroutineScope,
        targetCoordinate: LatLng,
        onAnimationComplete: () -> Unit
    ) {
        // [START maps_android_ktx_coroutines_animate]
        scope.launch {
            // Suspend execution until the maps camera animation finishes
            googleMap.awaitAnimateCamera(
                CameraUpdateFactory.newLatLngZoom(targetCoordinate, 15f)
            )
            // Continues execution only after camera stops moving!
            onAnimationComplete()
        }
        // [END maps_android_ktx_coroutines_animate]
    }

    /**
     * Demonstrates listening to camera idle changes reactively via Flow.
     */
    public fun cameraIdleEventsFlowSnippet(
        googleMap: GoogleMap,
        scope: CoroutineScope,
        onCameraIdle: () -> Unit
    ) {
        // [START maps_android_ktx_flow_camera_events]
        scope.launch {
            googleMap.cameraIdleEvents().collect {
                // Reacts immediately when map camera becomes stationary
                onCameraIdle()
            }
        }
        // [END maps_android_ktx_flow_camera_events]
    }

    /**
     * Checks if a coordinate falls inside a Map Polygon boundary.
     */
    public fun polygonContainsCheckSnippet(
        polygon: Polygon,
        coordinate: LatLng
    ): Boolean {
        // [START maps_android_ktx_utils_polygon_contains]
        val isInsideBoundary: Boolean = polygon.contains(coordinate)
        return isInsideBoundary
        // [END maps_android_ktx_utils_polygon_contains]
    }

    /**
     * Builds a styled GeoJSON layer dynamically.
     */
    public fun buildGeoJsonLayerSnippet(
        googleMap: GoogleMap,
        context: Context,
        resourceId: Int
    ): GeoJsonLayer {
        // [START maps_android_ktx_utils_geojson]
        val layer: GeoJsonLayer = geoJsonLayer(
            map = googleMap,
            resourceId = resourceId,
            context = context
        )
        return layer
        // [END maps_android_ktx_utils_geojson]
    }
}
