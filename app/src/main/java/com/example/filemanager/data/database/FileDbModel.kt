package com.example.filemanager.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class FileDbModel(
    @PrimaryKey
    val path: String,
    val hashCode: String
)