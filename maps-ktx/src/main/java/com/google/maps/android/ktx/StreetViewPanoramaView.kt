package com.google.maps.android.ktx

import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import com.google.android.gms.maps.model.StreetViewPanoramaLocation
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A suspending function that provides an instance of a [StreetViewPanorama] from this
 * [StreetViewPanoramaView]. This is an alternative to using
 * [StreetViewPanoramaView.getStreetViewPanoramaAsync] by using coroutines to obtain a
 * [StreetViewPanorama].
 *
 * @return the [StreetViewPanorama] instance
 */
public suspend inline fun StreetViewPanoramaView.awaitStreetViewPanorama(): StreetViewPanorama =
    suspendCoroutine { continuation ->
        getStreetViewPanoramaAsync {
            continuation.resume(it)
        }
    }

/**
 * Returns a flow that emits when the street view panorama camera changes. Using this to
 * observe panorama camera change events will override an existing listener (if any) to
 * [StreetViewPanorama.setOnStreetViewPanoramaCameraChangeListener].
 */
public fun StreetViewPanorama.cameraChangeEvents(): Flow<StreetViewPanoramaCamera> =
    callbackFlow {
        setOnStreetViewPanoramaCameraChangeListener {
            trySend(it)
        }
        awaitClose {
            setOnStreetViewPanoramaCameraChangeListener(null)
        }
    }

/**
 * Returns a flow that emits when the street view panorama loads a new panorama. Using this to
 * observe panorama load change events will override an existing listener (if any) to
 * [StreetViewPanorama.setOnStreetViewPanoramaChangeListener].
 */
public fun StreetViewPanorama.changeEvents(): Flow<StreetViewPanoramaLocation> =
    callbackFlow {
        setOnStreetViewPanoramaChangeListener {
            trySend(it)
        }
        awaitClose {
            setOnStreetViewPanoramaChangeListener(null)
        }
    }

/**
 * Returns a flow that emits when the street view panorama is clicked. Using this to
 * observe panorama click events will override an existing listener (if any) to
 * [StreetViewPanorama.setOnStreetViewPanoramaClickListener].
 */
public fun StreetViewPanorama.clickEvents(): Flow<StreetViewPanoramaOrientation> =
    callbackFlow {
        setOnStreetViewPanoramaClickListener {
            trySend(it)
        }
        awaitClose {
            setOnStreetViewPanoramaClickListener(null)
        }
    }

/**
 * Returns a flow that emits when the street view panorama is long clicked. Using this to
 * observe panorama long click events will override an existing listener (if any) to
 * [StreetViewPanorama.setOnStreetViewPanoramaLongClickListener].
 */
public fun StreetViewPanorama.longClickEvents(): Flow<StreetViewPanoramaOrientation> =
    callbackFlow {
        setOnStreetViewPanoramaLongClickListener {
            trySend(it)
        }
        awaitClose {
            setOnStreetViewPanoramaLongClickListener(null)
        }
    }