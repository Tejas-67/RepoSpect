package com.example.repospect.UI

import Session
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import com.example.repospect.DataModel.User
import com.example.repospect.Repository.AuthRepository

class AuthViewModel(
    private val application: Application,
    private val repository: AuthRepository,
    private val context: Context
): AndroidViewModel(application) {

    private val session = Session.getInstance(context)

    fun updateImage(image: String){
        session.updateUserImage(image)
    }
    fun updateUserName(username: String) {
        session.updateUserName(username)
    }

    fun isLogin():Boolean = session.isLogin()

    fun createSession(user: User){
        user.apply {
            session.createSession(name, email, image, id)
        }
    }

    fun getUserId(): String = session.getUserId()
    fun getUserImage(): Uri = session.getUserImage()
    fun getUserName(): String = session.getUserName()
    fun getEmail(): String = session.getEmail()



    fun signOut(){
        session.signOut()
    }

    fun loginUser(email: String, password: String, callback: (User?, String?)-> Unit){
        repository.login(email, password){ res, err ->
            callback(res, err)
        }
    }
    fun signupUser(email: String, name: String, password: String, image: String, callback: (User?, String?)->Unit){
        repository.signup(name, email, password, image){ res, err ->
            callback(res, err)
        }
    }
    fun changePassword(email: String, currPassword: String, newPassword: String, callback: (User?, String?) -> Unit){
        repository.changePassword(email, currPassword, newPassword){res, err ->
            callback(res, err)
        }
    }
    fun changeUsername(email: String, newUsername: String, password: String, callback: (User?, String?) -> Unit){
        repository.changeUsername(email, newUsername, password){res, err ->
            callback(res, err)
        }
    }

    fun updateProfilePic(email: String, newImage: String, callback: (User?, String?)->Unit){
        repository.changeProfilePicture(email, newImage){ res, err ->
            callback(res, err)
        }
    }

    fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork?:return false
            val capability = connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
            return when{
                capability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run{
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ContactsContract.CommonDataKinds.Email.TYPE_MOBILE ->  true
                    ConnectivityManager.TYPE_ETHERNET ->  true
                    else -> false
                }
            }
        }
        return false
    }
}