package com.example.repospect.DataModel

data class Issue(
    val created_at: String,
    val id: Int,
    val node_id: String,
    val number: Int,
    val url: String,
    val user: User,
    val title: String
)