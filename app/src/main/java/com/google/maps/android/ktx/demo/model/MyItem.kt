/*
 * Copyright 2023 Google Inc.
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

package com.google.maps.android.ktx.demo.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * A data class that implements the [ClusterItem] interface so it can be clustered.
 *
 *  Unfortunately Kotlin data class fields do not override Java interface methods by default
 *  (https://youtrack.jetbrains.com/issue/KT-6653?_ga=2.30406975.1494223917.1585591891-1137021041.1573759593)
 *  so we must name our fields differently and then pass them to the ClusterItem methods.
 */
data class MyItem(val latLng: LatLng, val myTitle: String?, val mySnippet: String?, val myZIndex: Float?) : ClusterItem {
    override fun getPosition() = latLng
    override fun getTitle() = myTitle
    override fun getSnippet() = mySnippet
    override fun getZIndex() = myZIndex
}