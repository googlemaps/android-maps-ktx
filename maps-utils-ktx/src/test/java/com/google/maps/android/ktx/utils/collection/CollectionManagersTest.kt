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

package com.google.maps.android.ktx.utils.collection

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline
import com.google.common.truth.Truth.assertThat
import com.google.maps.android.collections.CircleManager
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
public class CollectionManagersTest {

    @Mock
    private lateinit var markerCollection: MarkerManager.Collection

    @Mock
    private lateinit var polylineCollection: PolylineManager.Collection

    @Mock
    private lateinit var polygonCollection: PolygonManager.Collection

    @Mock
    private lateinit var circleCollection: CircleManager.Collection

    @Mock
    private lateinit var groundOverlayCollection: GroundOverlayManager.Collection

    @Mock
    private lateinit var marker: Marker

    @Mock
    private lateinit var polyline: Polyline

    @Mock
    private lateinit var polygon: Polygon

    @Mock
    private lateinit var circle: Circle

    @Mock
    private lateinit var groundOverlay: GroundOverlay

    @Captor
    private lateinit var markerClickListener: ArgumentCaptor<GoogleMap.OnMarkerClickListener>

    @Captor
    private lateinit var infoWindowClickListener: ArgumentCaptor<GoogleMap.OnInfoWindowClickListener>

    @Captor
    private lateinit var infoWindowLongClickListener: ArgumentCaptor<GoogleMap.OnInfoWindowLongClickListener>

    @Captor
    private lateinit var polylineClickListener: ArgumentCaptor<GoogleMap.OnPolylineClickListener>

    @Captor
    private lateinit var polygonClickListener: ArgumentCaptor<GoogleMap.OnPolygonClickListener>

    @Captor
    private lateinit var circleClickListener: ArgumentCaptor<GoogleMap.OnCircleClickListener>

    @Captor
    private lateinit var groundOverlayClickListener: ArgumentCaptor<GoogleMap.OnGroundOverlayClickListener>

    @Test
    public fun testMarkerCollectionClickEvents(): Unit = runTest {
        val job = launch {
            val event = markerCollection.clickEvents().first()
            assertThat(event).isEqualTo(marker)
        }
        advanceUntilIdle()
        verify(markerCollection).setOnMarkerClickListener(markerClickListener.capture())
        markerClickListener.value.onMarkerClick(marker)
        job.cancel()
    }

    @Test
    public fun testMarkerCollectionInfoWindowClickEvents(): Unit = runTest {
        val job = launch {
            val event = markerCollection.infoWindowClickEvents().first()
            assertThat(event).isEqualTo(marker)
        }
        advanceUntilIdle()
        verify(markerCollection).setOnInfoWindowClickListener(infoWindowClickListener.capture())
        infoWindowClickListener.value.onInfoWindowClick(marker)
        job.cancel()
    }

    @Test
    public fun testMarkerCollectionInfoWindowLongClickEvents(): Unit = runTest {
        val job = launch {
            val event = markerCollection.infoWindowLongClickEvents().first()
            assertThat(event).isEqualTo(marker)
        }
        advanceUntilIdle()
        verify(markerCollection).setOnInfoWindowLongClickListener(infoWindowLongClickListener.capture())
        infoWindowLongClickListener.value.onInfoWindowLongClick(marker)
        job.cancel()
    }

    @Test
    public fun testPolylineCollectionClickEvents(): Unit = runTest {
        val job = launch {
            val event = polylineCollection.clickEvents().first()
            assertThat(event).isEqualTo(polyline)
        }
        advanceUntilIdle()
        verify(polylineCollection).setOnPolylineClickListener(polylineClickListener.capture())
        polylineClickListener.value.onPolylineClick(polyline)
        job.cancel()
    }

    @Test
    public fun testPolygonCollectionClickEvents(): Unit = runTest {
        val job = launch {
            val event = polygonCollection.clickEvents().first()
            assertThat(event).isEqualTo(polygon)
        }
        advanceUntilIdle()
        verify(polygonCollection).setOnPolygonClickListener(polygonClickListener.capture())
        polygonClickListener.value.onPolygonClick(polygon)
        job.cancel()
    }

    @Test
    public fun testCircleCollectionClickEvents(): Unit = runTest {
        val job = launch {
            val event = circleCollection.clickEvents().first()
            assertThat(event).isEqualTo(circle)
        }
        advanceUntilIdle()
        verify(circleCollection).setOnCircleClickListener(circleClickListener.capture())
        circleClickListener.value.onCircleClick(circle)
        job.cancel()
    }

    @Test
    public fun testGroundOverlayCollectionClickEvents(): Unit = runTest {
        val job = launch {
            val event = groundOverlayCollection.clickEvents().first()
            assertThat(event).isEqualTo(groundOverlay)
        }
        advanceUntilIdle()
        verify(groundOverlayCollection).setOnGroundOverlayClickListener(groundOverlayClickListener.capture())
        groundOverlayClickListener.value.onGroundOverlayClick(groundOverlay)
        job.cancel()
    }
}
