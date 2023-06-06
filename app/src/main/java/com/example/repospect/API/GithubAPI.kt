package com.example.repospect.API

import com.example.repospect.DataModel.Repositories
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
    )

}