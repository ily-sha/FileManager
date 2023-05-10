package com.example.filemanager.presenter

import android.app.Application
import android.os.Environment
import android.util.Log
import com.example.filemanager.data.DatabaseImpl
import com.example.filemanager.data.database.FileDbModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest


class FileDifferent(private val application: Application) {
    private val dbImpl = DatabaseImpl(application)
    private var filesList = mutableListOf<FileDbModel>()


    private fun File.computeMd5(): String {
        val md = MessageDigest.getInstance("MD5")
        return inputStream().use { fis ->
            while (fis.available() != 0) {
                val buffer = ByteArray(8192)
                fis.read(buffer)
                md.update(buffer)
            }
            md.digest().joinToString("") { "%02x".format(it) }
        }
    }

    val coroutineScope = CoroutineScope(Dispatchers.Default)
    lateinit var parentJob: Job
    val changedFiles = mutableListOf<File>()
    private var isCountNewFiles = true
    val newFiles = mutableListOf<File>()
        get() = if (filesList.isEmpty()) mutableListOf() else field


    suspend fun getCountChangedFile() {
        val job = coroutineScope.async {
            filesList = dbImpl.getFiles().toMutableList()
            if (filesList.size == 0) {
                isCountNewFiles = false
            }
            val rootFolder = File(Environment.getExternalStorageDirectory().absolutePath)
            val job = launch {
                parseFileTree(rootFolder, this.coroutineContext.job)
            }
            job.join()
        }
        parentJob = job.job
        parentJob.join()

    }


    private fun parseFileTree(folder: File, parentJob: Job) {

        if (folder.listFiles() != null) {
            for (file in folder.listFiles()!!) {
                if (file.isDirectory) {
                    coroutineScope.launch(parentJob) {
                        parseFileTree(file, this.coroutineContext.job)
                    }
                } else {

                    coroutineScope.launch(parentJob) {
                        val fileHash = file.computeMd5()
                        filesList.find { it.path == file.absolutePath }.let {
                            if (it != null) {
                                if (it.hashCode != fileHash) {
                                    val entity = FileDbModel(file.absolutePath, fileHash)
                                    synchronized(dbImpl) {
                                        dbImpl.updateFile(entity)
                                    }
                                    synchronized(changedFiles) {

                                        changedFiles.add(file)
                                    }

                                }
                            } else {
                                synchronized(newFiles) {
                                    newFiles.add(file)
                                }
                                synchronized(dbImpl) {
                                    dbImpl.addFile(
                                        FileDbModel(
                                            file.absolutePath,
                                            fileHash
                                        )
                                    )
                                }


                            }
                        }
                    }
                }
            }
        }

    }

}
