package com.example.repospect.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.repospect.DataModel.Repo

@Database(entities = [Repo::class], version=1, exportSchema = false)
abstract class RepoDatabase : RoomDatabase() {

    abstract fun getDao(): RepoDao

    companion object{

        @Volatile
        private var INSTANCE: RepoDatabase? = null


        fun getDatabase(context: Context): RepoDatabase{
            if(INSTANCE!=null) return INSTANCE!!

            val instance = Room.databaseBuilder(context.applicationContext, RepoDatabase::class.java, "repo_database")
                .allowMainThreadQueries().build()
            INSTANCE=instance

            return instance
        }
    }
}