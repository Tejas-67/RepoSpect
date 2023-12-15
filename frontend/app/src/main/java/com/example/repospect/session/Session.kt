import android.content.Context
import android.net.Uri
import com.example.repospect.DataModel.User

class Session private constructor(context: Context) {
    private val sharedPref = context.getSharedPreferences("repospect_session", Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()

    fun isLogin(): Boolean {
        return sharedPref.getBoolean(IS_LOGIN, false)
    }

    fun getUserId(): String {
        return sharedPref.getString(USER_ID, "") ?: ""
    }

    fun createSession(username: String, email: String, image: String, userid: String) {
        editor.putString(USERNAME, username)
        editor.putString(EMAIL, email)
        editor.putString(IMAGE, image)
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(USER_ID, userid)
        editor.apply()
    }

    fun getUser(): User {
        val username = sharedPref.getString(USERNAME, "") ?: ""
        val email = sharedPref.getString(EMAIL, "") ?: ""
        val image = sharedPref.getString(IMAGE, "") ?: ""
        val password = sharedPref.getString(PASSWORD, "") ?: ""
        val userID = sharedPref.getString(USER_ID, "") ?: ""
        return User(name = username, email = email, image = image, password = password, id = userID)
    }

    fun updateUserImage(newImage: String){
        editor.putString(IMAGE, newImage)
        editor.apply()
    }

    fun updateUserName(newUsername: String){
        editor.putString(USERNAME, newUsername)
        editor.apply()
    }


    fun getUserImage(): Uri {
        return Uri.parse(sharedPref.getString(IMAGE, ""))
    }

    fun signOut() {
        editor.clear()
        editor.apply()
    }

    fun getUserName(): String = sharedPref.getString(USERNAME, "")!!
    fun getEmail(): String = sharedPref.getString(EMAIL, "")!!

    companion object {
        private var instance: Session? = null

        fun getInstance(context: Context): Session {
            return instance ?: synchronized(this) {
                instance ?: Session(context).also { instance = it }
            }
        }

        const val IS_LOGIN = "isLoggedIn"
        const val USER_ID = "userid"
        const val USERNAME = "username"
        const val EMAIL = "useremail"
        const val IMAGE = "userimage"
        const val PASSWORD = "password"
    }
}
