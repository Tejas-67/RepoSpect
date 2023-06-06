package com.example.repospect.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.repospect.DataModel.Repo

@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repo: Repo)

    @Delete
    suspend fun deleteRepo(repo: Repo)

    @Query("SELECT * FROM Repo ORDER BY pid ASC")
    fun getAllRepos(): LiveData<List<Repo>>

}