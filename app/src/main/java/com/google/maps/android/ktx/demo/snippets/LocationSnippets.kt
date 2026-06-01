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

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import com.google.maps.android.ktx.utils.location.coarseLocationEvents
import com.google.maps.android.ktx.utils.location.fineLocationEvents
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Standalone snippets demonstrating device location flows tracking in maps-utils-ktx.
 */
public object LocationSnippets {

    /**
     * Demonstrates coarse location tracking from network/passive provider.
     */
    @SuppressLint("MissingPermission")
    public fun coarseLocationFlowSnippet(
        locationManager: LocationManager,
        scope: CoroutineScope,
        onLocationReceived: (Location) -> Unit,
        onGPSDisabled: () -> Unit
    ) {
        // [START maps_android_ktx_flow_coarse_location]
        scope.launch {
            try {
                // Collect from the coarseLocationEvents cold flow
                locationManager.coarseLocationEvents(minTimeMs = 5000L, minDistanceM = 5f)
                    .collect { location ->
                        onLocationReceived(location)
                    }
            } catch (e: CancellationException) {
                // Triggers if the coarse location provider was disabled mid-collection
                onGPSDisabled()
            }
        }
        // [END maps_android_ktx_flow_coarse_location]
    }

    /**
     * Demonstrates fine location tracking from GPS.
     */
    @SuppressLint("MissingPermission")
    public fun fineLocationFlowSnippet(
        locationManager: LocationManager,
        scope: CoroutineScope,
        onLocationReceived: (Location) -> Unit,
        onGPSDisabled: () -> Unit
    ) {
        // [START maps_android_ktx_flow_fine_location]
        scope.launch {
            try {
                // Collect from the fineLocationEvents cold flow
                locationManager.fineLocationEvents(minTimeMs = 2000L, minDistanceM = 1f)
                    .collect { location ->
                        onLocationReceived(location)
                    }
            } catch (e: CancellationException) {
                // Triggers if GPS was disabled in settings mid-collection
                onGPSDisabled()
            }
        }
        // [END maps_android_ktx_flow_fine_location]
    }
}
