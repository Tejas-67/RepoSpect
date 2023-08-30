package com.example.repospect.API

import com.example.repospect.DataModel.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginAPI {

    @POST("/login")
    fun loginUsingEmailAndPassword(@Body map: HashMap<String, String>): Response<UserData>

    @POST("/signup")
    fun signUpNewUser(@Body map: HashMap<String, String>): Response<UserData>

}