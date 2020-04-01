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