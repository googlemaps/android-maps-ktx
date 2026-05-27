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

import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.collections.PolylineManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Returns a flow that emits when a polyline in this collection is clicked. Using this to observe polyline clicks
 * will override an existing listener (if any) to [PolylineManager.Collection.setOnPolylineClickListener].
 */
public fun PolylineManager.Collection.clickEvents(): Flow<Polyline> =
    callbackFlow {
        setOnPolylineClickListener {
            trySend(it).isSuccess
        }
        awaitClose {
            setOnPolylineClickListener(null)
        }
    }
