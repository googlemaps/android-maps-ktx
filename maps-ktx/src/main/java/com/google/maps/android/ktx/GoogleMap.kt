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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.ktx.model.buildMarkerOptions
import com.google.maps.android.ktx.model.buildPolygonOptions

/**
 * Adds a [Marker] to this [GoogleMap] using the [MarkerOptions] specified in the [optionsActions]
 * lambda.
 *
 * @return the added [Marker]
 */
inline fun GoogleMap.addMarker(optionsActions: MarkerOptions.() -> Unit): Marker =
    this.addMarker(
        buildMarkerOptions(optionsActions)
    )

/**
 * Adds a [Polygon] to this [GoogleMap] using the [PolygonOptions] specified in the [optionsActions]
 * lambda.
 *
 * @return the added [Polygon]
 */
inline fun GoogleMap.addPolygon(optionsActions: PolygonOptions.() -> Unit): Polygon =
    this.addPolygon(
        buildPolygonOptions(optionsActions)
    )
