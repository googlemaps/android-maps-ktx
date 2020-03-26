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
 *
 */

package com.google.maps.android.ktx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.ktx.model.buildCircleOptions
import com.google.maps.android.ktx.model.buildGroundOverlayOptions
import com.google.maps.android.ktx.model.buildMarkerOptions
import com.google.maps.android.ktx.model.buildPolygonOptions
import com.google.maps.android.ktx.model.buildPolylineOptions
import com.google.maps.android.ktx.model.buildTileOverlayOptions

/**
 * Builds a new [GoogleMapOptions] using the provided [optionsActions].
 *
 * @return the constructed [GoogleMapOptions]
 */
inline fun buildGoogleMapOptions(optionsActions: GoogleMapOptions.() -> Unit): GoogleMapOptions =
    GoogleMapOptions().apply(
        optionsActions
    )

/**
 * Adds a [Circle] to this [GoogleMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Circle]
 */
inline fun GoogleMap.addCircle(optionsActions: CircleOptions.() -> Unit): Circle =
    this.addCircle(
        buildCircleOptions(optionsActions)
    )

/**
 * Adds a [GroundOverlay] to this [GoogleMap] using the function literal with receiver
 * [optionsActions].
 *
 * @return the added [Circle]
 */
inline fun GoogleMap.addGroundOverlay(
    optionsActions: GroundOverlayOptions.() -> Unit
): GroundOverlay =
    this.addGroundOverlay(
        buildGroundOverlayOptions(optionsActions)
    )

/**
 * Adds a [Marker] to this [GoogleMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Marker]
 */
inline fun GoogleMap.addMarker(optionsActions: MarkerOptions.() -> Unit): Marker =
    this.addMarker(
        buildMarkerOptions(optionsActions)
    )

/**
 * Adds a [Polygon] to this [GoogleMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Polygon]
 */
inline fun GoogleMap.addPolygon(optionsActions: PolygonOptions.() -> Unit): Polygon =
    this.addPolygon(
        buildPolygonOptions(optionsActions)
    )

/**
 * Adds a [Polyline] to this [GoogleMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Polyline]
 */
inline fun GoogleMap.addPolyline(optionsActions: PolylineOptions.() -> Unit): Polyline =
    this.addPolyline(
        buildPolylineOptions(optionsActions)
    )

/**
 * Adds a [TileOverlay] to this [GoogleMap] using the function literal with receiver
 * [optionsActions].
 *
 * @return the added [Polyline]
 */
inline fun GoogleMap.addTileOverlay(optionsActions: TileOverlayOptions.() -> Unit): TileOverlay =
    this.addTileOverlay(
        buildTileOverlayOptions(optionsActions)
    )
