package com.example.repospect.UI

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repospect.DataModel.*
import com.example.repospect.Repository.RepoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class RepoViewModel(
    val repository: RepoRepository
) : ViewModel() {

    var allBranches: MutableLiveData<Resource<Branches>> = MutableLiveData()
    var allLocalRepo: LiveData<List<Repo>> = repository.allSavedRepo
    var searchRepositoriesResponse: MutableLiveData<Resource<Repositories>> = MutableLiveData()
    var searchedRepo: MutableLiveData<Resource<Repo>> = MutableLiveData()
    var currentRepoIssues: MutableLiveData<Resource<Issues>> = MutableLiveData()
    fun addNewRepoToLocal(repo: Repo){
        viewModelScope.launch(Dispatchers.IO){
            repository.checkIfElementExists(repo)
        }
    }

    fun deleteRepoFromLocal(repo: Repo){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteRepoFromLocal(repo)
        }
    }

    fun searchWithKeyWord(key: String) {
        viewModelScope.launch {

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

    fun handleBranchResponse(response: Response<Branches>): Resource<Branches>{
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun handleIssueResponse(response: Response<Issues>): Resource<Issues>{
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
        return Resource.Error(response.message())
    }
}