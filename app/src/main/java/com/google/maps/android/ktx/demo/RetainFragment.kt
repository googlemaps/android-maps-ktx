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
 */

package com.google.maps.android.ktx.demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.maps.android.data.Renderer.ImagesCache

/**
 * Fragment for retaining the bitmap cache between configuration changes.
 * See https://developer.android.com/topic/performance/graphics/cache-bitmap#config-changes.
 */
class RetainFragment : Fragment() {
    var mImagesCache: ImagesCache? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    companion object {
        private val TAG = RetainFragment::class.java.name
        fun findOrCreateRetainFragment(fm: FragmentManager): RetainFragment {
            return (fm.findFragmentByTag(TAG) as? RetainFragment) ?: run {
                RetainFragment().also {
                    fm.beginTransaction().add(it, TAG).commit()
                }
            }
        }
    }
}