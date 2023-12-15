package com.example.repospect.UI

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repospect.Repository.RepoRepository

class RepoViewModelProviderFactory(val application: Application, val repo: RepoRepository, val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RepoViewModel(application, repo, context) as T
    }
}