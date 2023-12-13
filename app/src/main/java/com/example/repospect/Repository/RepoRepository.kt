package com.example.repospect.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.repospect.API.RetrofitInstance
import com.example.repospect.DataModel.*
import com.example.repospect.Database.RepoDao
import com.example.repospect.Database.RepoDatabase
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoRepository(db: RepoDatabase) {
    private val dao: RepoDao = db.getDao()

    private val retrofit = RetrofitInstance

    var allSavedRepo: LiveData<List<Repo>> = dao.getAllRepos()

    suspend fun addNewRepo(repo: Repo){
        dao.insertRepo(repo)
    }
    suspend fun getUpdatedData(): List<Repo>{
        Log.w("RepoSpectWorkManager", "getUpdatedData reached")
        val newList: ArrayList<Repo> = arrayListOf()
        val currentRepos = dao.getAllReposSync()
        Log.w("RepoSpectWorkManager", "$currentRepos")
        Log.w("RepospectWorkManager", "size of currentlist: ${currentRepos.size.toString()}")
        for(repo in currentRepos){
            val iden = repo.full_name!!.split('/')
            val newRepo = getRepoWithOwnerAndRepoName(iden[0], iden[1])
            if(newRepo.isSuccessful){
                newList.add(newRepo.body()!!)
                Log.w("RepospectWorkManager", newRepo.body()!!.description.toString())
            }
            else newList.add(repo)
        }
        Log.w("RepoSpectWorkManager", "size of newList: ${newList.size}")
        return newList
    }
    suspend fun getRepoWithOwnerAndRepoName(owner: String, repoName: String): Response<Repo>{
        return RetrofitInstance.githubApi.getRepoUsingOwnerNameAndRepoName(owner, repoName)
    }
    suspend fun deleteRepoFromLocal(repo: Repo){
        dao.deleteRepo(repo)
    }
    suspend fun loginUser(password:String, email:String): Call<User> {
        return retrofit.loginApi.loginUser(email, password)
    }

    fun login(email: String, password: String, callback: (User?, String?)-> Unit){
        val call = retrofit.loginApi.loginUser(email, password)

        call.enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("auth-logs", "$response")
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

    fun signup(name: String, email: String, password: String, callback: (User?, String?)-> Unit){
        val call = retrofit.loginApi.signupUser(name, email, password)

        call.enqueue(object: Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    val user = response.body()
                    callback(user, null)
                }
                else{
                    Log.w("auth-logs", "$response")
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

    suspend fun checkIfElementExists(repo: Repo){
        if(dao.checkElement(repo.pid)==null) addNewRepo(repo)
    }
    suspend fun getIssues(owner: String, name: String): Response<Issues>{
        return RetrofitInstance.githubApi.getIssues(owner, name)
    }
    suspend fun searchForKeyword(key: String): Response<Repositories>{
        return RetrofitInstance.githubApi.searchRepo(key)
    }

    suspend fun getBranches(owner: String, name: String): Response<Branches> {
        return RetrofitInstance.githubApi.getBranchesForRepo(owner, name)
    }

    suspend fun getCommits(owner: String, name: String, branchName: String): Response<Commits>{
        return RetrofitInstance.githubApi.getCommits(owner, name, branchName)
    }

    suspend fun updateAllRepos(list: List<Repo>){
        dao.updateAllRepos(list)
    }
}