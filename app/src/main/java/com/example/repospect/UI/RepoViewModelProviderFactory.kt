package com.example.repospect.UI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repospect.Repository.RepoRepository

class RepoViewModelProviderFactory(val repo: RepoRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RepoViewModel(repo) as T
    }
}