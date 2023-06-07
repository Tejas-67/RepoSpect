package com.example.repospect.Repository

import androidx.lifecycle.LiveData
import com.example.repospect.API.RetrofitInstance
import com.example.repospect.DataModel.Repo
import com.example.repospect.DataModel.Repositories
import com.example.repospect.Database.RepoDao
import com.example.repospect.Database.RepoDatabase
import retrofit2.Response

class RepoRepository(val db: RepoDatabase) {
    private val dao: RepoDao = db.getDao()

    var allSavedRepo: LiveData<List<Repo>> = dao.getAllRepos()

    suspend fun addNewRepo(repo: Repo){
        dao.insertRepo(repo)
    }

    suspend fun deleteRepoFromLocal(repo: Repo){
        dao.deleteRepo(repo)
    }

    suspend fun searchForKeyword(key: String): Response<Repositories>{
        return RetrofitInstance.api.searchRepo(key)
    }

    suspend fun getRepoWithOwnerAndRepoName(owner: String, repoName: String): Response<Repo>{
        return RetrofitInstance.api.getRepoUsingOwnerNameAndRepoName(owner, repoName)
    }
}