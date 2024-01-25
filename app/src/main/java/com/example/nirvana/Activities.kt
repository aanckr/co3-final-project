package com.example.nirvana

data class Activities(
    var name: String,
    var category: String,
    var rank: Int,
    var tags: List<String>,
    var latitude: Double,
    var longitude: Double,
    var id: String
) {
    constructor() : this("", "", 0, listOf<String>(), 0.0, 0.0, "")
}