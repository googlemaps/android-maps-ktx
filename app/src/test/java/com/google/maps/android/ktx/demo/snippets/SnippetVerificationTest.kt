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

package com.google.maps.android.ktx.demo.snippets

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.content.Context
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.CircleManager
import com.google.maps.android.collections.MarkerManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.eq
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
public class SnippetVerificationTest {

    // Location Mocks
    @Mock
    private lateinit var locationManager: LocationManager
    @Mock
    private lateinit var location: Location
    @Captor
    private lateinit var locationListenerCaptor: ArgumentCaptor<LocationListener>

    // Clustering Mocks
    @Mock
    private lateinit var clusterManager: ClusterManager<ClusterItem>
    @Mock
    private lateinit var cluster: Cluster<ClusterItem>
    @Mock
    private lateinit var clusterItem: ClusterItem
    @Captor
    private lateinit var clusterClickListener: ArgumentCaptor<ClusterManager.OnClusterClickListener<ClusterItem>>

    // Shape Collections Mocks
    @Mock
    private lateinit var markerCollection: MarkerManager.Collection
    @Mock
    private lateinit var circleCollection: CircleManager.Collection
    @Mock
    private lateinit var marker: Marker
    @Mock
    private lateinit var circle: Circle

    // Existing KTX Mocks
    @Mock
    private lateinit var googleMap: GoogleMap
    @Mock
    private lateinit var mapFragment: SupportMapFragment
    @Mock
    private lateinit var polygon: Polygon
    @Mock
    private lateinit var context: Context
    @Captor
    private lateinit var onMapReadyCallbackCaptor: ArgumentCaptor<OnMapReadyCallback>
    @Captor
    private lateinit var cameraIdleListenerCaptor: ArgumentCaptor<GoogleMap.OnCameraIdleListener>
    @Captor
    private lateinit var cancelableCallbackCaptor: ArgumentCaptor<GoogleMap.CancelableCallback>

    @Mock
    private lateinit var cameraUpdate: CameraUpdate
    private lateinit var cameraUpdateFactoryMock: MockedStatic<CameraUpdateFactory>

    private var activeClusterItemClickListener: ClusterManager.OnClusterItemClickListener<ClusterItem>? = null
    private var activeMarkerClickListener: GoogleMap.OnMarkerClickListener? = null
    private var activeCircleClickListener: GoogleMap.OnCircleClickListener? = null

    @Before
    public fun setUp() {
        cameraUpdateFactoryMock = mockStatic(CameraUpdateFactory::class.java)
        `when`(CameraUpdateFactory.newLatLngZoom(any(), anyFloat())).thenReturn(cameraUpdate)

        activeClusterItemClickListener = null
        activeMarkerClickListener = null
        activeCircleClickListener = null

        // Stub setOnClusterItemClickListener to capture its active listener
        `when`(clusterManager.setOnClusterItemClickListener(any())).thenAnswer { invocation ->
            activeClusterItemClickListener = invocation.arguments[0] as? ClusterManager.OnClusterItemClickListener<ClusterItem>
            null
        }

        // Stub setOnMarkerClickListener
        `when`(markerCollection.setOnMarkerClickListener(any())).thenAnswer { invocation ->
            activeMarkerClickListener = invocation.arguments[0] as? GoogleMap.OnMarkerClickListener
            null
        }

        // Stub setOnCircleClickListener
        `when`(circleCollection.setOnCircleClickListener(any())).thenAnswer { invocation ->
            activeCircleClickListener = invocation.arguments[0] as? GoogleMap.OnCircleClickListener
            null
        }
    }

    @After
    public fun tearDown() {
        cameraUpdateFactoryMock.close()
    }

    @SuppressLint("MissingPermission")
    @Test
    public fun testCoarseLocationFlowSnippet(): Unit = runTest {
        `when`(locationManager.allProviders).thenReturn(listOf(LocationManager.NETWORK_PROVIDER))
        val results = mutableListOf<Location>()
        var isCanceled = false

        LocationSnippets.coarseLocationFlowSnippet(
            locationManager,
            scope = this,
            onLocationReceived = { results.add(it) },
            onGPSDisabled = { isCanceled = true }
        )
        advanceUntilIdle()

        verify(locationManager).requestLocationUpdates(
            eq(LocationManager.NETWORK_PROVIDER),
            anyLong(),
            anyFloat(),
            locationListenerCaptor.capture()
        )

        // Trigger event
        locationListenerCaptor.value.onLocationChanged(location)
        advanceUntilIdle()
        assertThat(results).containsExactly(location)

        // Simulate disablement
        locationListenerCaptor.value.onProviderDisabled(LocationManager.NETWORK_PROVIDER)
        advanceUntilIdle()
        assertThat(isCanceled).isTrue()
    }

    @SuppressLint("MissingPermission")
    @Test
    public fun testFineLocationFlowSnippet(): Unit = runTest {
        val results = mutableListOf<Location>()
        var isCanceled = false

        LocationSnippets.fineLocationFlowSnippet(
            locationManager,
            scope = this,
            onLocationReceived = { results.add(it) },
            onGPSDisabled = { isCanceled = true }
        )
        advanceUntilIdle()

        verify(locationManager).requestLocationUpdates(
            eq(LocationManager.GPS_PROVIDER),
            anyLong(),
            anyFloat(),
            locationListenerCaptor.capture()
        )

        locationListenerCaptor.value.onLocationChanged(location)
        advanceUntilIdle()
        assertThat(results).containsExactly(location)
        coroutineContext.job.cancelChildren()
    }

    @Test
    public fun testClusterClicksSnippet(): Unit = runTest {
        val results = mutableListOf<Cluster<ClusterItem>>()
        
        ClusteringSnippets.clusterClicksSnippet(
            clusterManager,
            scope = this,
            onClusterClicked = { results.add(it) }
        )
        advanceUntilIdle()

        verify(clusterManager).setOnClusterClickListener(clusterClickListener.capture())
        clusterClickListener.value.onClusterClick(cluster)
        advanceUntilIdle()

        assertThat(results).containsExactly(cluster)
        coroutineContext.job.cancelChildren()
    }

    @Test
    public fun testClusterItemClicksSnippet(): Unit = runTest {
        val obs1Results = mutableListOf<ClusterItem>()
        val obs2Results = mutableListOf<ClusterItem>()

        ClusteringSnippets.clusterItemClicksSnippet(
            clusterManager,
            scope = this,
            onObserverOneReceived = { obs1Results.add(it) },
            onObserverTwoReceived = { obs2Results.add(it) }
        )
        advanceUntilIdle()

        // Verify shared flow broadcasted the click to BOTH observers!
        assertThat(activeClusterItemClickListener).isNotNull()
        activeClusterItemClickListener?.onClusterItemClick(clusterItem)
        advanceUntilIdle()

        assertThat(obs1Results).containsExactly(clusterItem)
        assertThat(obs2Results).containsExactly(clusterItem)
        coroutineContext.job.cancelChildren()
    }

    @Test
    public fun testMarkerCollectionClicksSnippet(): Unit = runTest {
        val results = mutableListOf<Marker>()

        CollectionSnippets.markerCollectionClicksSnippet(
            markerCollection,
            scope = this,
            onMarkerClicked = { results.add(it) }
        )
        advanceUntilIdle()

        assertThat(activeMarkerClickListener).isNotNull()
        activeMarkerClickListener?.onMarkerClick(marker)
        advanceUntilIdle()

        assertThat(results).containsExactly(marker)
        coroutineContext.job.cancelChildren()
    }

    @Test
    public fun testCircleCollectionClicksSnippet(): Unit = runTest {
        val results = mutableListOf<Circle>()

        CollectionSnippets.circleCollectionClicksSnippet(
            circleCollection,
            scope = this,
            onCircleClicked = { results.add(it) }
        )
        advanceUntilIdle()

        assertThat(activeCircleClickListener).isNotNull()
        activeCircleClickListener?.onCircleClick(circle)
        advanceUntilIdle()

        assertThat(results).containsExactly(circle)
        coroutineContext.job.cancelChildren()
    }

    @Test
    public fun testAwaitMapInitSnippet(): Unit = runTest {
        val mapReadyList = mutableListOf<GoogleMap>()
        
        ExistingKtxSnippets.awaitMapInitSnippet(
            mapFragment,
            scope = this,
            onMapReady = { mapReadyList.add(it) }
        )
        advanceUntilIdle()

        verify(mapFragment).getMapAsync(onMapReadyCallbackCaptor.capture())
        onMapReadyCallbackCaptor.value.onMapReady(googleMap)
        advanceUntilIdle()

        assertThat(mapReadyList).containsExactly(googleMap)
        coroutineContext.job.cancelChildren()
    }

    @Test
    public fun testAwaitCameraAnimationSnippet(): Unit = runTest {
        var animationComplete = false
        val target = LatLng(51.5, -0.1)

        ExistingKtxSnippets.awaitCameraAnimationSnippet(
            googleMap,
            scope = this,
            targetCoordinate = target,
            onAnimationComplete = { animationComplete = true }
        )
        advanceUntilIdle()

        verify(googleMap).animateCamera(any(CameraUpdate::class.java), eq(3000), cancelableCallbackCaptor.capture())
        cancelableCallbackCaptor.value.onFinish()
        advanceUntilIdle()

        assertThat(animationComplete).isTrue()
        coroutineContext.job.cancelChildren()
    }

    @Test
    public fun testCameraIdleEventsFlowSnippet(): Unit = runTest {
        var cameraIdleCount = 0

        ExistingKtxSnippets.cameraIdleEventsFlowSnippet(
            googleMap,
            scope = this,
            onCameraIdle = { cameraIdleCount++ }
        )
        advanceUntilIdle()

        verify(googleMap).setOnCameraIdleListener(cameraIdleListenerCaptor.capture())
        cameraIdleListenerCaptor.value.onCameraIdle()
        advanceUntilIdle()

        assertThat(cameraIdleCount).isEqualTo(1)
        coroutineContext.job.cancelChildren()
    }

    @Test
    public fun testPolygonContainsCheckSnippet(): Unit = runTest {
        val target = LatLng(51.5074, -0.1278)
        // Setup real polygon points list to test the real geometry check engine!
        val polygonPoints = listOf(
            LatLng(51.52, -0.14),
            LatLng(51.52, -0.11),
            LatLng(51.49, -0.11),
            LatLng(51.49, -0.14)
        )
        `when`(polygon.points).thenReturn(polygonPoints)

        val inside = ExistingKtxSnippets.polygonContainsCheckSnippet(polygon, target)
        assertThat(inside).isTrue()
    }
}
