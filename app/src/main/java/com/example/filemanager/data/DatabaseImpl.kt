package com.example.filemanager.data

import android.app.Application
import com.example.filemanager.data.database.FileDatabase
import com.example.filemanager.data.database.FileDbModel
import java.util.LinkedList

class DatabaseImpl(application: Application) {

    val dao = FileDatabase.getInstance(application).fileDao()

    fun getFiles(): List<FileDbModel> {
        return dao.getFiles()
    }

    fun addFile(dbModel: FileDbModel){
        dao.addFile(dbModel)
    }

    fun updateFile(dbModel: FileDbModel){
        dao.updateFile(dbModel)
    }

    fun removeFile(dbModel: FileDbModel){
        dao.removeFile(dbModel)
    }
}