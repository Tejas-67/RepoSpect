package com.example.repospect.DataModel

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity
@Parcelize
data class Repo(
    val branches_url: String, // branches of the repo
    val clone_url: String, // clone link
    val language: String, //programming language used the most.
    val commits_url: String, // all commits of the repo
    val contributors_url: String, //all contributors of the repo
    val created_at: String, //date created
    val default_branch: String, //keep
    val description: String, // Desc of the repo
    val forks_count: Int, //number of forks
    val full_name: String, // "owner/repo" name
    val git_commits_url: String, // see all commits
    val html_url: String, // actual github url
    @PrimaryKey(autoGenerate = true)
    val pid: Int, //primary key for database
    val id: Int, //primary key
    val issues_url: String, //for issue in each repo
    val name: String,
    val open_issues_count: Int, //number of open issues
    val size: Int, //size of repo in KB
    val ssh_url: String,

    //converters should be added for insering list<String>
//    val topics: List<String>, //topics related to this repo


    val updated_at: String, //last updated at data
    val url: String,
    val visibility: String, // public or private.
) : Parcelable