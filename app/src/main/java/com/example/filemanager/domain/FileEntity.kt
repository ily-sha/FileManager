package com.example.filemanager.domain

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.File


data class FileEntity(
    val name: String,
    val timeCreated: Long,
    val absolutePath: String,
    val isDirectory: Boolean,
    val isFile: Boolean,
    val size: Long,
    val uri: Uri,
    val bitmap: Bitmap?= null,
    val drawable: Drawable?= null,
    val fileType: FileType
)