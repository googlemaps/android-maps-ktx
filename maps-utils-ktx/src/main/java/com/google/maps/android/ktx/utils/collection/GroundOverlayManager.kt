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

package com.google.maps.android.ktx.utils.collection

import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.maps.android.collections.GroundOverlayManager

/**
 * Adds a new [GroundOverlay] to the underlying map and to this [GroundOverlayManager.Collection]
 * with the provided [optionsActions].
 */
inline fun GroundOverlayManager.Collection.addGroundOverlay(
    optionsActions: GroundOverlayOptions.() -> Unit
): GroundOverlay =
    this.addGroundOverlay(
        GroundOverlayOptions().apply(optionsActions)
    )