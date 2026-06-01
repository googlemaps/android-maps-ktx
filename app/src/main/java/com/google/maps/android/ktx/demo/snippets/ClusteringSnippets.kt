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

import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.utils.clustering.clusterClickEvents
import com.google.maps.android.ktx.utils.clustering.clusterItemClickEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

/**
 * Standalone snippets demonstrating ClusterManager flow events in maps-utils-ktx.
 */
public object ClusteringSnippets {

    /**
     * Demonstrates listening to cluster click events.
     */
    public fun <T : ClusterItem> clusterClicksSnippet(
        clusterManager: ClusterManager<T>,
        scope: CoroutineScope,
        onClusterClicked: (Cluster<T>) -> Unit
    ) {
        // [START maps_android_ktx_flow_cluster_clicks]
        scope.launch {
            clusterManager.clusterClickEvents().collect { cluster ->
                // Handle the clicked cluster containing multiple items
                onClusterClicked(cluster)
            }
        }
        // [END maps_android_ktx_flow_cluster_clicks]
    }

    /**
     * Demonstrates listening to cluster item click events, including how to share the cold
     * flow into a Hot SharedFlow to support multiple observers safely.
     */
    public fun <T : ClusterItem> clusterItemClicksSnippet(
        clusterManager: ClusterManager<T>,
        scope: CoroutineScope,
        onObserverOneReceived: (T) -> Unit,
        onObserverTwoReceived: (T) -> Unit
    ) {
        // [START maps_android_ktx_flow_cluster_item_clicks]
        // Wrap the KTX cold flow into a Hot SharedFlow to safely share a single listener slot across multiple observers
        val sharedItemClicks: SharedFlow<T> = clusterManager.clusterItemClickEvents()
            .shareIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 0
            )

        // Observer 1
        scope.launch {
            sharedItemClicks.collect { item ->
                onObserverOneReceived(item)
            }
        }

        // Observer 2
        scope.launch {
            sharedItemClicks.collect { item ->
                onObserverTwoReceived(item)
            }
        }
        // [END maps_android_ktx_flow_cluster_item_clicks]
    }
}
