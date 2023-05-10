package com.example.filemanager.presenter

import android.app.Application
import android.os.Environment
import android.util.Log
import com.example.filemanager.data.DatabaseImpl
import com.example.filemanager.data.database.FileDbModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest


class FileDifferent(private val application: Application) {
    private val TAG = "TAGGGGGGG"
    private val dbImpl = DatabaseImpl(application)
    private var filesList = mutableListOf<FileMatcher>()

    //DCIM/Camera/VID_20210627_051753.mp4
    private fun File.computeMd5(): String {
        val md = MessageDigest.getInstance("MD5")
        return inputStream().use { fis ->
            while (fis.available() != 0) {
//                val buffer = ByteArray(8192)
                val buffer = ByteArray(8192)
                fis.read(buffer)
                md.update(buffer)
            }
            md.digest().joinToString("") { "%02x".format(it) }
        }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var changedFile = mutableListOf<File>()

    data class FileMatcher(
        val path: String,
        val hashCode: String,
        val founded: Boolean = false
    )

    suspend fun getCountChangedFile(): MutableList<File> {
        val deferred = coroutineScope.async {
            Log.d(TAG, "start")

            filesList = dbImpl.getFiles().map {
                FileMatcher(
                    path = it.path,
                    hashCode = it.hashCode
                )
            }.toMutableList()
            val rootFolder = File(Environment.getExternalStorageDirectory().absolutePath + "/Pictures")

            val job = launch {
                parseFileTree(rootFolder, this.coroutineContext.job)
                parseFileTree(File(Environment.getExternalStorageDirectory().absolutePath + "/VK"), this.coroutineContext.job)
            }
            job.join()
            removeFile()
            changedFile
        }
        return deferred.await()
    }




    private fun removeFile() {
        filesList.forEach {
            if (!it.founded) dbImpl.removeFile(
                FileDbModel(
                    path = it.path,
                    hashCode = it.hashCode
                )
            )
        }
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
                        Log.d(TAG, fileHash)
                        file.getIndexAndElementByPath().let {
                            if (it != null) {
                                val (match, indexMatch) = it
                                if (match.hashCode != fileHash) {
                                    val entity = FileDbModel(file.absolutePath, fileHash)
                                    dbImpl.updateFile(entity)
                                    synchronized(changedFile) {
                                        changedFile.add(file)
                                    }
                                    changedFile
                                    filesList[indexMatch] = match.copy(founded = true)
                                }
                            } else {
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
//        else {
//            parentJob.cancel()
//        }

    }


    private fun File.getIndexAndElementByPath(): Pair<FileMatcher, Int>? {
        for (j in filesList.indices) {
            if (filesList[j].path == absolutePath) {
                return filesList[j] to j
            }
        }
        return null
    }


}
