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

import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.collections.CircleManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.utils.collection.clickEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Standalone snippets demonstrating collection manager flow click events in maps-utils-ktx.
 */
public object CollectionSnippets {

    /**
     * Demonstrates listening to marker click events on a styled Marker collection.
     */
    public fun markerCollectionClicksSnippet(
        markerCollection: MarkerManager.Collection,
        scope: CoroutineScope,
        onMarkerClicked: (Marker) -> Unit
    ) {
        // [START maps_android_ktx_flow_marker_collection_clicks]
        scope.launch {
            markerCollection.clickEvents().collect { marker ->
                // Handle clicking a marker belonging to this collection
                onMarkerClicked(marker)
            }
        }
        // [END maps_android_ktx_flow_marker_collection_clicks]
    }

    /**
     * Demonstrates listening to circle click events on a styled Circle collection.
     */
    public fun circleCollectionClicksSnippet(
        circleCollection: CircleManager.Collection,
        scope: CoroutineScope,
        onCircleClicked: (Circle) -> Unit
    ) {
        // [START maps_android_ktx_flow_circle_collection_clicks]
        scope.launch {
            circleCollection.clickEvents().collect { circle ->
                // Handle clicking a circle belonging to this collection
                onCircleClicked(circle)
            }
        }
        // [END maps_android_ktx_flow_circle_collection_clicks]
    }
}
