/*
 * Copyright 2021 Google Inc.
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

package com.google.maps.android.ktx.components

import com.google.maps.android.ktx.demo.components.toFeet
import com.google.maps.android.ktx.demo.components.toMiles
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class ScaleBarTest {

    @Test
    fun testUnits() {
        val delta = 0.0000001

        val mile = toMiles(5280.0)
        assertEquals(1.0, mile, delta)

        // Ensure a "0 miles" value never appears on the map
        assertTrue(mile >= 1.0)

        val lessThanMile = toMiles(5279.999999)
        assertTrue(lessThanMile < 1.0)

        val meters = 1000.0
        val kilometers = meters / 1000.0.toInt()
        // Ensure a "0 kilometers" value never appears on the map
        assertTrue(kilometers >= 1.0)

        val feet = toFeet(1.0)
        assertEquals(3.280839895, feet, delta)
    }
}