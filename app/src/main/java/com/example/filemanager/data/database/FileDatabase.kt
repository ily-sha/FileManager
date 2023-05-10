package com.example.filemanager.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [FileDbModel::class], version = 1)
abstract class FileDatabase: RoomDatabase() {

    abstract fun fileDao(): FileDao
    companion object{
        private var instance: FileDatabase? = null
        private const val name = "files.db"
        private val LOCK = Any()

        fun getInstance(application: Application): FileDatabase {
            instance?.let {
                return it
            }
            synchronized(LOCK) {
                instance?.let {
                    return it
                }
                instance = Room.databaseBuilder(application, FileDatabase::class.java, name)
                    .build()
                return instance as FileDatabase
            }
        }
    }


}