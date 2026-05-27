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

package com.google.maps.android.ktx.utils.clustering

import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Returns a flow that emits when a cluster is clicked. Using this to observe cluster clicks
 * will override an existing listener (if any) to [ClusterManager.setOnClusterClickListener].
 */
public fun <T : ClusterItem> ClusterManager<T>.clusterClickEvents(): Flow<Cluster<T>> =
    callbackFlow {
        setOnClusterClickListener {
            trySend(it).isSuccess
        }
        awaitClose {
            setOnClusterClickListener(null)
        }
    }

/**
 * Returns a flow that emits when a cluster item is clicked. Using this to observe cluster item clicks
 * will override an existing listener (if any) to [ClusterManager.setOnClusterItemClickListener].
 */
public fun <T : ClusterItem> ClusterManager<T>.clusterItemClickEvents(): Flow<T> =
    callbackFlow {
        setOnClusterItemClickListener {
            trySend(it).isSuccess
        }
        awaitClose {
            setOnClusterItemClickListener(null)
        }
    }

/**
 * Returns a flow that emits when a cluster's info window is clicked. Using this to observe cluster info window clicks
 * will override an existing listener (if any) to [ClusterManager.setOnClusterInfoWindowClickListener].
 */
public fun <T : ClusterItem> ClusterManager<T>.clusterInfoWindowClickEvents(): Flow<Cluster<T>> =
    callbackFlow {
        setOnClusterInfoWindowClickListener {
            trySend(it).isSuccess
        }
        awaitClose {
            setOnClusterInfoWindowClickListener(null)
        }
    }

/**
 * Returns a flow that emits when a cluster's info window is long clicked. Using this to observe cluster info window long clicks
 * will override an existing listener (if any) to [ClusterManager.setOnClusterInfoWindowLongClickListener].
 */
public fun <T : ClusterItem> ClusterManager<T>.clusterInfoWindowLongClickEvents(): Flow<Cluster<T>> =
    callbackFlow {
        setOnClusterInfoWindowLongClickListener {
            trySend(it).isSuccess
        }
        awaitClose {
            setOnClusterInfoWindowLongClickListener(null)
        }
    }

/**
 * Returns a flow that emits when a cluster item's info window is clicked. Using this to observe cluster item info window clicks
 * will override an existing listener (if any) to [ClusterManager.setOnClusterItemInfoWindowClickListener].
 */
public fun <T : ClusterItem> ClusterManager<T>.clusterItemInfoWindowClickEvents(): Flow<T> =
    callbackFlow {
        setOnClusterItemInfoWindowClickListener {
            trySend(it).isSuccess
        }
        awaitClose {
            setOnClusterItemInfoWindowClickListener(null)
        }
    }

/**
 * Returns a flow that emits when a cluster item's info window is long clicked. Using this to observe cluster item info window long clicks
 * will override an existing listener (if any) to [ClusterManager.setOnClusterItemInfoWindowLongClickListener].
 */
public fun <T : ClusterItem> ClusterManager<T>.clusterItemInfoWindowLongClickEvents(): Flow<T> =
    callbackFlow {
        setOnClusterItemInfoWindowLongClickListener {
            trySend(it).isSuccess
        }
        awaitClose {
            setOnClusterItemInfoWindowLongClickListener(null)
        }
    }
