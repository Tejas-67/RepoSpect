package com.example.repospect.WorkManager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.repospect.Database.RepoDatabase
import com.example.repospect.Repository.RepoRepository
import kotlinx.coroutines.CoroutineScope

class LocalDataUpdateWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {
    private val repository = RepoRepository(RepoDatabase.getDatabase(appContext))

    override suspend fun doWork(): Result {
        return try{
            Log.w("RepoSpectWorkManager", "$repository")
            val updatedData = repository.getUpdatedData()
            Log.w("RepoSpectWorkManager", "updatedData: $updatedData")
            repository.updateAllRepos(updatedData)
            Log.w("RepoSpectWorkManager", "updateAllRepos called")
            Result.success()
        } catch(e: Exception){
            Log.w("RepoSpectWorkManager", "WM Fail: $e")
            Result.failure()
        }
    }
}