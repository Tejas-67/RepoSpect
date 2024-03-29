package com.example.repospect.API

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GithubRetrofitInstance {
    companion object
    {
        val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client=OkHttpClient.Builder().addInterceptor(logging).build()

            Retrofit.Builder().baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        val api by lazy {
            retrofit.create(GithubAPI::class.java)
        }
    }
}