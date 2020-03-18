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

package com.google.maps.android.ktx

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil

/**
 * Computes whether the given [latLng] lies on or is near this polyline within [tolerance] (in
 * meters).
 *
 * @param latLng the LatLng to inspect
 * @param geodesic if this polyline is geodesic or not
 * @param tolerance the tolerance in meters
 * @return true if [latLng] is on this path, otherwise, false
 *
 * @see PolyUtil.isLocationOnPath
 */
inline fun List<LatLng>.isLocationOnPath(
    latLng: LatLng,
    geodesic: Boolean,
    tolerance: Double = 0.1
): Boolean = PolyUtil.isLocationOnPath(latLng, this, geodesic, tolerance)

/**
 * Checks whether or not [latLng] lies on or is near the edge of this polygon within the [tolerance]
 * (in meters). The default value is [PolyUtil.DEFAULT_TOLERANCE].
 *
 * @param latLng the LatLng to inspect
 * @param geodesic if this polygon is geodesic or not
 * @param tolerance the tolerance in meters
 * @return true if [latLng] lies on or is near the edge of this Polygon, otherwise, false
 *
 * @see PolyUtil.isLocationOnEdge
 */
inline fun List<LatLng>.isOnEdge(
    latLng: LatLng,
    geodesic: Boolean,
    tolerance: Double = 0.1
): Boolean = PolyUtil.isLocationOnEdge(latLng, this, geodesic, tolerance)

/**
 * Computes whether the [latLng] lies inside this.
 *
 * The polygon is always considered closed, regardless of whether the last point equals
 * the first or not.
 *
 * Inside is defined as not containing the South Pole -- the South Pole is always outside.
 * The polygon is formed of great circle segments if [geodesic] is true, and of rhumb
 * (loxodromic) segments otherwise.
 *
 * @param latLng the LatLng to check if it is contained within this polygon
 * @param geodesic if this Polygon is geodesic or not
 *
 * @see PolyUtil.containsLocation
 */
inline fun List<LatLng>.containsLocation(latLng: LatLng, geodesic: Boolean): Boolean =
    PolyUtil.containsLocation(latLng, this, geodesic)

/**
 * Simplifies this list of LatLng using the Douglas-Peucker decimation. Increasing the value of
 * [tolerance] will result in fewer points.
 *
 * @param tolerance the tolerance in meters
 * @return the simplified list of [LatLng]
 *
 * @see PolyUtil.simplify
 */
inline fun List<LatLng>.simplify(tolerance: Double): List<LatLng> =
    PolyUtil.simplify(this, tolerance)

/**
 * Decodes this encoded string into a [LatLng] list.
 *
 * @return the decoded [LatLng] list
 *
 * @see [Polyline Algorithm Format](https://developers.google.com/maps/documentation/utilities/polylinealgorithm)
 */
inline fun String.toLatLngList(): List<LatLng> = PolyUtil.decode(this)

/**
 * Encodes this [LatLng] list in a String using the
 * [Polyline Algorithm Format](https://developers.google.com/maps/documentation/utilities/polylinealgorithm).
 *
 * @return the encoded String
 *
 * @see [Polyline Algorithm Format](https://developers.google.com/maps/documentation/utilities/polylinealgorithm)
 */
inline fun List<LatLng>.latLngListEncode(): String = PolyUtil.encode(this)

/**
 * Checks whether or not this [LatLng] list is a closed Polygon.
 *
 * @return true if this list is a closed Polygon, otherwise, false
 *
 * @see PolyUtil.isClosedPolygon
 */
inline fun List<LatLng>.isClosedPolygon(): Boolean = PolyUtil.isClosedPolygon(this)

/**
 * Computes the length of this path on Earth.
 *
 * @return the length of this path in meters
 */
inline fun List<LatLng>.sphericalPathLength(): Double = SphericalUtil.computeLength(this)

/**
 * Computes the area under a closed path on Earth.
 *
 * @return the area in square meters
 */
inline fun List<LatLng>.sphericalPolygonArea(): Double = SphericalUtil.computeArea(this)

/**
 * Computes the signed area under a closed path on Earth. The sign of the area may be used to
 * determine the orientation of the path.
 *
 * @return the signed area in square meters
 */
inline fun List<LatLng>.sphericalPolygonSignedArea(): Double = SphericalUtil.computeSignedArea(this)

/**
 * Computes the heading from this LatLng to [toLatLng].
 *
 * @param toLatLng the other LatLng to compute the heading to
 * @return the heading expressed in degrees clockwise from North within the range [-180, 180]
 *
 * @see SphericalUtil.computeHeading
 */
inline fun LatLng.sphericalHeading(toLatLng: LatLng): Double =
    SphericalUtil.computeHeading(this, toLatLng)

/**
 * Offsets this LatLng from the provided [distance] and [heading] and returns the result.
 *
 * @param distance the distance to offset by in meters
 * @param heading the heading to offset by in degrees clockwise from north
 * @return the resulting LatLng
 *
 * @see SphericalUtil.computeOffset
 */
inline fun LatLng.withSphericalOffset(distance: Double, heading: Double): LatLng =
    SphericalUtil.computeOffset(this, distance, heading)

/**
 * Attempts to compute the origin [LatLng] from this LatLng where [distance] meters have been
 * traveled with heading value [heading].
 *
 * @param distance the distance traveled from origin in meters
 * @param heading the heading from origin to this LatLng (measured in degrees clockwise from North)
 * @return the computed origin if a solution is available, otherwise, null
 *
 * @see SphericalUtil.computeOffsetOrigin
 */
inline fun LatLng.computeSphericalOffsetOrigin(distance: Double, heading: Double): LatLng? =
    SphericalUtil.computeOffsetOrigin(this, distance, heading)

/**
 * Returns an interpolated [LatLng] between this LatLng and [to] by the provided fractional value
 * [fraction].
 *
 * @param to the destination LatLng
 * @param fraction the fraction to interpolate by where the range is [0.0, 1.0]
 * @return the interpolated [LatLng]
 *
 * @see [Slerp](http://en.wikipedia.org/wiki/Slerp)
 */
inline fun LatLng.withSphericalLinearInterpolation(to: LatLng, fraction: Double): LatLng =
    SphericalUtil.interpolate(this, to, fraction)

/**
 * Computes the spherical distance between this LatLng and [to].
 *
 * @param to the LatLng to compute the distance to
 * @return the distance between this and [to] in meters
 */
inline fun LatLng.sphericalDistance(to: LatLng): Double =
    SphericalUtil.computeDistanceBetween(this, to)
