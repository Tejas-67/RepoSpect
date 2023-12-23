package com.example.repospect.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.repospect.DataModel.Repo

@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repo: Repo)

    @Query("SELECT * FROM Repo WHERE pid = :elementId LIMIT 1")
    suspend fun checkElement(elementId: Int): Repo

    @Delete
    suspend fun deleteRepo(repo: Repo)

    @Query("SELECT * FROM Repo ORDER BY pid ASC")
    fun getAllRepos(): LiveData<List<Repo>>

    @Update
    fun updateAllRepos(list: List<Repo>)

    @Query("SELECT * FROM Repo")
    fun getAllReposSync(): List<Repo>

}