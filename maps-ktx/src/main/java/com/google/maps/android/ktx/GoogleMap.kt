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

import androidx.annotation.IntDef
import com.google.android.gms.maps.CameraUpdate
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
import com.google.maps.android.ktx.model.circleOptions
import com.google.maps.android.ktx.model.groundOverlayOptions
import com.google.maps.android.ktx.model.markerOptions
import com.google.maps.android.ktx.model.polygonOptions
import com.google.maps.android.ktx.model.polylineOptions
import com.google.maps.android.ktx.model.tileOverlayOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@IntDef(
    GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE,
    GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION,
    GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION
)
@Retention(AnnotationRetention.SOURCE)
public annotation class MoveStartedReason

public sealed class CameraEvent
public object CameraIdleEvent : CameraEvent()
public object CameraMoveCanceledEvent : CameraEvent()
public object CameraMoveEvent : CameraEvent()
public data class CameraMoveStartedEvent(@MoveStartedReason val reason: Int) : CameraEvent()

// Since offer() can throw when the channel is closed (channel can close before the
// block within awaitClose), wrap `offer` calls inside `runCatching`.
// See: https://github.com/Kotlin/kotlinx.coroutines/issues/974
private fun <E> SendChannel<E>.offerCatching(element: E): Boolean {
    return runCatching { offer(element) }.getOrDefault(false)
}

/**
 * Returns a [Flow] of [CameraEvent] items so that camera movements can be observed. Using this to
 * observe camera events will set listeners and thus override existing listeners to
 * [GoogleMap.setOnCameraIdleListener], [GoogleMap.setOnCameraMoveCanceledListener],
 * [GoogleMap.setOnCameraMoveListener] and [GoogleMap.setOnCameraMoveStartedListener].
 */
@ExperimentalCoroutinesApi
public fun GoogleMap.cameraEvents(): Flow<CameraEvent> =
    callbackFlow {
        setOnCameraIdleListener {
            offerCatching(CameraIdleEvent)
        }
        setOnCameraMoveCanceledListener {
            offerCatching(CameraMoveCanceledEvent)
        }
        setOnCameraMoveListener {
            offerCatching(CameraMoveEvent)
        }
        setOnCameraMoveStartedListener {
            offerCatching(CameraMoveStartedEvent(it))
        }
        awaitClose {
            setOnCameraIdleListener(null)
            setOnCameraMoveCanceledListener(null)
            setOnCameraMoveListener(null)
            setOnCameraMoveStartedListener(null)
        }
    }

/**
 * A suspending function that awaits the completion of the [cameraUpdate] animation.
 *
 * @param cameraUpdate the [CameraUpdate] to apply on the map
 * @param durationMs the duration in milliseconds of the animation. Defaults to 3 seconds.
 */
public suspend inline fun GoogleMap.awaitAnimation(
    cameraUpdate: CameraUpdate,
    durationMs: Int = 3000
): Unit =
    suspendCancellableCoroutine { continuation ->
        animateCamera(cameraUpdate, durationMs, object : GoogleMap.CancelableCallback {
            override fun onFinish() {
                continuation.resume(Unit)
            }

            override fun onCancel() {
                continuation.cancel()
            }
        })
    }


/**
 * Builds a new [GoogleMapOptions] using the provided [optionsActions].
 *
 * @return the constructed [GoogleMapOptions]
 */
public inline fun buildGoogleMapOptions(optionsActions: GoogleMapOptions.() -> Unit): GoogleMapOptions =
    GoogleMapOptions().apply(
        optionsActions
    )

/**
 * Adds a [Circle] to this [GoogleMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Circle]
 */
public inline fun GoogleMap.addCircle(optionsActions: CircleOptions.() -> Unit): Circle =
    this.addCircle(
        circleOptions(optionsActions)
    )

/**
 * Adds a [GroundOverlay] to this [GoogleMap] using the function literal with receiver
 * [optionsActions].
 *
 * @return the added [Circle]
 */
public inline fun GoogleMap.addGroundOverlay(optionsActions: GroundOverlayOptions.() -> Unit): GroundOverlay =
    this.addGroundOverlay(
        groundOverlayOptions(optionsActions)
    )

/**
 * Adds a [Marker] to this [GoogleMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Marker]
 */
public inline fun GoogleMap.addMarker(optionsActions: MarkerOptions.() -> Unit): Marker =
    this.addMarker(
        markerOptions(optionsActions)
    )

/**
 * Adds a [Polygon] to this [GoogleMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Polygon]
 */
public inline fun GoogleMap.addPolygon(optionsActions: PolygonOptions.() -> Unit): Polygon =
    this.addPolygon(
        polygonOptions(optionsActions)
    )

/**
 * Adds a [Polyline] to this [GoogleMap] using the function literal with receiver [optionsActions].
 *
 * @return the added [Polyline]
 */
public inline fun GoogleMap.addPolyline(optionsActions: PolylineOptions.() -> Unit): Polyline =
    this.addPolyline(
        polylineOptions(optionsActions)
    )

/**
 * Adds a [TileOverlay] to this [GoogleMap] using the function literal with receiver
 * [optionsActions].
 *
 * @return the added [Polyline]
 */
public inline fun GoogleMap.addTileOverlay(optionsActions: TileOverlayOptions.() -> Unit): TileOverlay =
    this.addTileOverlay(
        tileOverlayOptions(optionsActions)
    )
