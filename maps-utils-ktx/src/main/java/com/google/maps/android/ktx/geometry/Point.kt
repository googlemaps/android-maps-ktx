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

@file:Suppress("NOTHING_TO_INLINE")
package com.google.maps.android.ktx.geometry

import com.google.maps.android.geometry.Point

/**
 * Returns the x value of this Point.
 *
 * e.g.
 *
 * ```
 * val (x, _) = point
 * ```
 */
inline operator fun Point.component1() = this.x

/**
 * Returns the y value of this Point.
 *
 * e.g.
 *
 * ```
 * val (_, y) = point
 */
inline operator fun Point.component2() = this.y
