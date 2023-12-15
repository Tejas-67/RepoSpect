package com.example.repospect.Repository

import Session
import android.content.Context
import android.util.Log
import com.example.repospect.API.RetrofitInstance
import com.example.repospect.DataModel.ErrorResponse
import com.example.repospect.DataModel.User
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val context: Context) {
    private val retrofit = RetrofitInstance
    private val session = Session.getInstance(context)

    fun login(email: String, password: String, callback: (User?, String?)-> Unit){
        val call = retrofit.loginApi.loginUser(email, password)

        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    Log.w("api-check ", "error ${response.errorBody()?:"null"}")
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }

    fun changePassword(email: String, currentPassword: String, newPassword: String, callback: (User?, String?)->Unit){
        val call = retrofit.loginApi.updatePassword(email, currentPassword, newPassword)
        call.enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }

    fun changeUsername(email: String, newUsername: String, password: String, callback: (User?, String?)-> Unit){
        val call = retrofit.loginApi.updateUsername(email, newUsername, password)

        call.enqueue(object :Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }
    fun changeProfilePicture(email: String, newImageUrl: String, callback: (User?, String?)->Unit){
        val call = retrofit.loginApi.updateProfilePic(email, newImageUrl)

        call.enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }

    fun signup(name: String, email: String, password: String, image: String, callback: (User?, String?)-> Unit){
        val call = retrofit.loginApi.signupUser(name, email, password, image)

        call.enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    Log.w("api-check", "s $response")
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("api-check", "ns $response")
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    val errorMessage = errorResponse?.message?: "Unknown error"
                    callback(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback(null, "${t.message}")
            }
        })
    }

}