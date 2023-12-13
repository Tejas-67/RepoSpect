package com.example.repospect.DataModel

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("__v") val v: Int,
    @SerializedName("_id") val id: String,
    val createdAt: String,
    val email: String,
    val name: String,
    val password: String,
    val updatedAt: String
)