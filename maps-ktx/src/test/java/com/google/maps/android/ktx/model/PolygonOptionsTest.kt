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

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test

class PolygonOptionsTest {
    @Test
    fun testBuilder() {
        val polygonOptions =
            polygonOptions {
                strokeWidth(1.0f)
                strokeColor(Color.BLACK)
                add(LatLng(1.0, 2.0))
            }
        assertEquals(1.0f, polygonOptions.strokeWidth, 1e-6f)
        assertEquals(Color.BLACK, polygonOptions.strokeColor)
        assertEquals(listOf(LatLng(1.0, 2.0)), polygonOptions.points)
    }
}