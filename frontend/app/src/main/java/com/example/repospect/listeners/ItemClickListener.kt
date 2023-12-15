package com.example.repospect.listeners

import android.view.View
import com.example.repospect.DataModel.Repo

interface ItemClickListener {
    fun onRepoClicked(view: View, repo: Repo)
    fun onBranchSelected(view: View, branchName: String)
    fun onDeleteButtonClicked(view: View, repo: Repo)
}