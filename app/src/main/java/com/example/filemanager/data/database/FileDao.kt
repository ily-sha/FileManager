package com.example.filemanager.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.util.LinkedList

@Dao
interface FileDao {

    @Query(value = "SELECT * FROM filedbmodel")
    fun getFiles(): List<FileDbModel>

    @Insert
    fun addFile(fileDbModel: FileDbModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFile(fileDbModel: FileDbModel)

    @Delete
    fun removeFile(fileDbModel: FileDbModel)

}