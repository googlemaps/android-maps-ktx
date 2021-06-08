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

package com.google.maps.android.ktx.utils.heatmap

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.heatmaps.WeightedLatLng
import com.google.maps.android.ktx.utils.heatmaps.toWeightedLatLng
import org.junit.Assert.*
import org.junit.Test

internal class HeatmapTest {
    @Test
    fun `to WeightedLatLng converts correctly`() {
        val latLng = LatLng(1.0, 2.0)

        weightedLatLngEquals(WeightedLatLng(latLng), latLng.toWeightedLatLng())
        weightedLatLngEquals(
            WeightedLatLng(latLng, 2.0),
            latLng.toWeightedLatLng(intensity = 2.0)
        )
    }

    private fun weightedLatLngEquals(lhs: WeightedLatLng, rhs: WeightedLatLng) {
        assertEquals(lhs.point.x, rhs.point.x, 1e-6)
        assertEquals(lhs.point.y, rhs.point.y, 1e-6)
        assertEquals(lhs.intensity, rhs.intensity, 1e-6)
    }
}