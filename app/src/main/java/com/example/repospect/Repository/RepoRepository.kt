package com.example.repospect.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.repospect.API.AuthenticationRetrofitInstance
import com.example.repospect.API.GithubRetrofitInstance
import com.example.repospect.DataModel.*
import com.example.repospect.Database.RepoDao
import com.example.repospect.Database.RepoDatabase
import retrofit2.Response

class RepoRepository(db: RepoDatabase) {
    private val dao: RepoDao = db.getDao()

    var allSavedRepo: LiveData<List<Repo>> = dao.getAllRepos()

    suspend fun addNewRepo(repo: Repo){
        dao.insertRepo(repo)
    }
    suspend fun getUpdatedData(): List<Repo>{
        val newList: ArrayList<Repo> = arrayListOf()
        val currentRepos = dao.getAllReposSync()
        for(repo in currentRepos){
            val iden = repo.full_name!!.split('/')
            val newRepo = getRepoWithOwnerAndRepoName(iden[0], iden[1])
            if(newRepo.isSuccessful){
                newList.add(newRepo.body()!!)
            }
            else newList.add(repo)
        }
        return newList
    }
    suspend fun getRepoWithOwnerAndRepoName(owner: String, repoName: String): Response<Repo>{
        return GithubRetrofitInstance.api.getRepoUsingOwnerNameAndRepoName(owner, repoName)
    }
    suspend fun searchRepository(searchText: String): Response<SearchResponse> {
        return GithubRetrofitInstance.api.searchRepo(searchText)
    }
    suspend fun deleteRepoFromLocal(repo: Repo){
        dao.deleteRepo(repo)
    }
    suspend fun getIssues(owner: String, name: String): Response<Issues>{
        return GithubRetrofitInstance.api.getIssues(owner, name)
    }

    suspend fun getBranches(owner: String, name: String): Response<Branches> {
        return GithubRetrofitInstance.api.getBranchesForRepo(owner, name)
    }

    suspend fun getCommits(owner: String, name: String, branchName: String): Response<Commits>{
        return GithubRetrofitInstance.api.getCommits(owner, name, branchName)
    }

    suspend fun updateAllRepos(list: List<Repo>){
        dao.updateAllRepos(list)
    }
}