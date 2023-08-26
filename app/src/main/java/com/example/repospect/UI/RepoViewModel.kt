package com.example.repospect.UI

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.repospect.DataModel.*
import com.example.repospect.Repository.RepoRepository
import com.example.repospect.WorkManager.LocalDataUpdateWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class RepoViewModel(
    private val application: Application,
    private val repository: RepoRepository,
    private val context: Context
) : AndroidViewModel(application) {

    private lateinit var workInfoObserver: Observer<WorkInfo>
    var allLocalRepo: LiveData<List<Repo>> = repository.allSavedRepo
    fun startLocalDataUpdate(){
        Log.w("RepoSpectWorkManager", "localdataupdatestarts")
        val updateRequest = OneTimeWorkRequestBuilder<LocalDataUpdateWorker>().build()
        WorkManager.getInstance(context).enqueue(updateRequest)
        workInfoObserver = Observer {
            if(it.state==WorkInfo.State.SUCCEEDED) {
                ifDataUpdated.postValue(true)
                Log.w("RepoSpectWorkManager", "localdataupdatesuccessfull")
                WorkManager.getInstance(context).getWorkInfoByIdLiveData(updateRequest.id)
                    .removeObserver(workInfoObserver)
                ifDataUpdated.postValue(true)
            }
        }
        ifDataUpdated.postValue(true)
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(updateRequest.id)
            .observeForever(workInfoObserver)
    }
    var ifDataUpdated: MutableLiveData<Boolean> = MutableLiveData(false)
    var allBranches: MutableLiveData<Resource<Branches>> = MutableLiveData()

    var searchRepositoriesResponse: MutableLiveData<Resource<Repositories>> = MutableLiveData()
    var searchedRepo: MutableLiveData<Resource<Repo>> = MutableLiveData()
    var currentRepoIssues: MutableLiveData<Resource<Issues>> = MutableLiveData()
    var currentRepoCommits: MutableLiveData<Resource<Commits>> = MutableLiveData()

    fun addNewRepoToLocal(repo: Repo){
        viewModelScope.launch(Dispatchers.IO){
            repository.addNewRepo(repo)
        }
    }

    fun deleteRepoFromLocal(repo: Repo){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteRepoFromLocal(repo)
        }
    }

    fun searchRepoWithOwnerAndName(owner: String, name: String){
        viewModelScope.launch {
            searchedRepo.postValue(Resource.Loading())
            val response = repository.getRepoWithOwnerAndRepoName(owner, name)
            searchedRepo.postValue(handleResponse(response))
        }
    }
    fun getAllBranches(owner: String, name: String){
        viewModelScope.launch{
            allBranches.postValue(Resource.Loading())
            val response=repository.getBranches(owner, name)
            allBranches.postValue(handleBranchResponse(response))
        }
    }

    fun getIssues(owner: String, name: String){
        viewModelScope.launch {
            currentRepoIssues.postValue(Resource.Loading())
            val response=repository.getIssues(owner, name)
            currentRepoIssues.postValue(handleIssueResponse(response))
        }
    }

    fun getCommits(owner: String, name: String, branch: String){
        viewModelScope.launch{
            currentRepoCommits.postValue(Resource.Loading())
            val response= repository.getCommits(owner, name, branch)
            currentRepoCommits.postValue(handleCommitsResponse(response))
        }
    }

    private fun handleCommitsResponse(response: Response<Commits>): Resource<Commits>{
        if(response.isSuccessful){
            response.body()?.let{
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }
    private fun handleBranchResponse(response: Response<Branches>): Resource<Branches>{
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleIssueResponse(response: Response<Issues>): Resource<Issues>{
        if (response.isSuccessful){
            response.body()?.let{
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }
    fun handleResponse(response: Response<Repo>): Resource<Repo>{
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        Log.w("RepoSpectWorkManager", "error while searching: ${response.message()}")
        return Resource.Error(response.message())
    }
}