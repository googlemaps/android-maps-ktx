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

package com.google.maps.android.ktx.model

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PolylineOptionsTest {

    @Test
    fun testBuilder() {
        val polylineOptions = polylineOptions {
            add(LatLng(1.0, 2.0))
            clickable(true)
            color(0)
            geodesic(true)
            width(1f)
        }
        assertEquals(listOf(LatLng(1.0, 2.0)), polylineOptions.points)
        assertTrue(polylineOptions.isClickable)
        assertEquals(0, polylineOptions.color)
        assertTrue(polylineOptions.isGeodesic)
        assertEquals(1f, polylineOptions.width, 1e-6f)
    }
}