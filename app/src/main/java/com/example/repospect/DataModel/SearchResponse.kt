package com.example.repospect.DataModel

data class SearchResponse(
    val incomplete_results: Boolean,
    val items: List<Repo>,
    val total_count: Int
)