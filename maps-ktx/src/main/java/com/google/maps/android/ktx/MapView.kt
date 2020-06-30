package com.google.maps.android.ktx

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A suspending function that provides an instance of [GoogleMap] from this [MapView]. This is
 * an alternative to [MapView.getMapAsync] by using coroutines to obtain the [GoogleMap].
 *
 * @return the [GoogleMap] instance
 */
suspend inline fun MapView.awaitMap(): GoogleMap =
    suspendCoroutine { continuation ->
        getMapAsync {
            continuation.resume(it)
        }
    }