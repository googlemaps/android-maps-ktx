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

package com.google.maps.android.ktx.demo.menu

data class SnippetItemInfo(
    val title: String,
    val description: String
)

data class SnippetGroupInfo(
    val title: String,
    val description: String,
    val items: List<SnippetItemInfo>
)

object SnippetRegistry {
    val groups: List<SnippetGroupInfo> by lazy {
        listOf(
            SnippetGroupInfo(
                title = "Map Initialization & Camera Control",
                description = "Coroutines and Flows managing lifecycle-aware map setups and camera glides.",
                items = listOf(
                    SnippetItemInfo(
                        title = "Map Initialization",
                        description = "Safely retrieves GoogleMap instance non-blockingly via awaitMap()."
                    ),
                    SnippetItemInfo(
                        title = "Animate Camera (Coroutines)",
                        description = "Suspends execution until map viewport glides and camera zoom complete."
                    ),
                    SnippetItemInfo(
                        title = "Camera Idle Events Flow",
                        description = "Streams stationary camera viewport events reactively using cameraIdleEvents()."
                    )
                )
            ),
            SnippetGroupInfo(
                title = "Kotlin Flow Location Services",
                description = "Hardware satellite and fused location streams managing lifecycles automatically.",
                items = listOf(
                    SnippetItemInfo(
                        title = "Fine Location Flow",
                        description = "High-precision reactive coordinate updates from fine provider."
                    ),
                    SnippetItemInfo(
                        title = "Coarse Location Flow",
                        description = "Wifi/Cell tower reactive coordinate updates from coarse/passive providers."
                    ),
                    SnippetItemInfo(
                        title = "Fused Location Flow",
                        description = "Google Play Services reactive location updates via FusedLocationProviderClient."
                    )
                )
            ),
            SnippetGroupInfo(
                title = "Shapes & Marker Clustering Click Event Flows",
                description = "Click handlers for overlays and marker clusters propagated dynamically via Flows.",
                items = listOf(
                    SnippetItemInfo(
                        title = "Marker Cluster Click Flow",
                        description = "Captures group cluster click events to dynamic expand/zoom operations."
                    ),
                    SnippetItemInfo(
                        title = "Marker Collection Click Flow",
                        description = "Propagates custom marker click flows in clean isolated sub-collections."
                    )
                )
            )
        )
    }
}
