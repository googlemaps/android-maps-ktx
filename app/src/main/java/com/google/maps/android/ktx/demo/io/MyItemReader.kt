/*
 * Copyright 2013 Google Inc.
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

package com.google.maps.android.ktx.demo.io

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.demo.model.MyItem
import org.json.JSONArray
import org.json.JSONException
import java.io.InputStream
import java.util.*

/**
 * Helper class to read in cluster items from a resource
 */
class MyItemReader {

    /**
     * Returns a list of cluster items read from the provided [inputStream]
     */
    @Throws(JSONException::class)
    fun read(inputStream: InputStream): List<MyItem> {
        // This matches only once in whole input so Scanner.next returns whole InputStream as a
        // String. http://stackoverflow.com/a/5445161/2183804
        val REGEX_INPUT_BOUNDARY_BEGINNING = "\\A"

        val items: MutableList<MyItem> = ArrayList()
        val json = Scanner(inputStream)
            .useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            var title: String? = null
            var snippet: String? = null
            val `object` = array.getJSONObject(i)
            val lat = `object`.getDouble("lat")
            val lng = `object`.getDouble("lng")
            if (!`object`.isNull("title")) {
                title = `object`.getString("title")
            }
            if (!`object`.isNull("snippet")) {
                snippet = `object`.getString("snippet")
            }
            items.add(MyItem(LatLng(lat, lng), title, snippet))
        }
        return items
    }
}