package com.example.nirvana

import android.health.connect.datatypes.DistanceRecord

data class Activities(
    var name: String? = null,
    var category: String? = null,
    var rank: Int? = null,
    var tags: List<String>? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var id: String? = null,
    var distance: Double? = null
)