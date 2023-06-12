package com.example.repospect.API

import com.example.repospect.DataModel.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubAPI {

    @GET("search/repositories")
    suspend fun searchRepo(
        @Query("q")
        q: String
    ): Response<Repositories>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepoUsingOwnerNameAndRepoName(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ):Response<Repo>

    @GET("repos/{owner}/{name}/branches")
    suspend fun getBranchesForRepo(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): Response<Branches>

    @GET("repos/{owner}/{name}/issues?state=open")
    suspend fun getIssues(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): Response<Issues>

    @GET("repos/{owner}/{name}/commits?")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("name") name: String,
        @Query("sha") branch: String
    ): Response<Commits>
}