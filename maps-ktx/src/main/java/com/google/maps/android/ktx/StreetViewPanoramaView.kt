package com.google.maps.android.ktx

import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanoramaView
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