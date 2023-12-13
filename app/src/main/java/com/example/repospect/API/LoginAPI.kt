package com.example.repospect.API

import android.util.Log
import com.example.repospect.DataModel.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginAPI {
    @GET("/login")
    fun loginUser(
        @Query("email") email: String,
        @Query("password") password: String
    ):Call<User>

    @GET("/signup")
    fun signupUser(
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<User>
}