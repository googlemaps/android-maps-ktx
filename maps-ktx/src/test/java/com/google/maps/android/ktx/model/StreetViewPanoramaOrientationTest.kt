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

import org.junit.Assert.assertEquals
import org.junit.Test

class StreetViewPanoramaOrientationTest {

    @Test
    fun `test that StreetViewPanoramaOrientation is constructed`() {
        val orientation = streetViewPanoramaOrientation {
            bearing(1f)
            tilt(20f)
        }
        assertEquals(1f, orientation.bearing, 1e-6f)
        assertEquals(20f, orientation.tilt, 1e-6f)
    }
}
