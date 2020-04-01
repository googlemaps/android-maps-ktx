package com.google.maps.android.ktx.demo.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class MyItem(val latLng: LatLng, val myTitle: String?, val mySnippet: String?) : ClusterItem {
    // Unfortunately Kotlin data class fields override Java interface methods by default
    // (https://youtrack.jetbrains.com/issue/KT-6653?_ga=2.30406975.1494223917.1585591891-1137021041.1573759593)
    // so we must map our fields with different names to the ClusterItem methods
    override fun getPosition() = latLng
    override fun getTitle() = myTitle
    override fun getSnippet() = mySnippet
}