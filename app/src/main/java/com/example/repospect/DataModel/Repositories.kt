package com.example.repospect.DataModel

data class Repositories(
    val incomplete_results: Boolean,
    val repos: List<Repo>,
    val total_count: Int
)