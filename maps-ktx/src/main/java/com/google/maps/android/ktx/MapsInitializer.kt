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
 */

package com.google.maps.android.ktx

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.MapsInitializer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Initializes the Maps SDK and returns the [MapsInitializer.Renderer] that was actually loaded.
 *
 * This function does not support cancellation because [MapsInitializer.initialize] cannot cancel
 * initialization. Only the first Maps SDK initialization in an application lifecycle honors
 * [preferredRenderer]; `null` uses the SDK's default preference.
 *
 * @param preferredRenderer the renderer to request, or `null` to use the default preference
 * @return the renderer actually loaded by the Maps SDK
 * @throws GooglePlayServicesNotAvailableException if initialization returns a status other than
 * [ConnectionResult.SUCCESS] without invoking the callback
 */
public suspend inline fun Context.awaitMapsSdkInitialized(
    preferredRenderer: MapsInitializer.Renderer? = null
): MapsInitializer.Renderer =
    suspendCoroutine { continuation ->
        var callbackInvoked = false
        val status = MapsInitializer.initialize(this, preferredRenderer) { renderer ->
            callbackInvoked = true
            continuation.resume(renderer)
        }
        if (!callbackInvoked && status != ConnectionResult.SUCCESS) {
            continuation.resumeWithException(GooglePlayServicesNotAvailableException(status))
        }
    }
