package com.example.filemanager.domain

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File
import java.io.Serializable

@Parcelize
data class FileEntity(
    val name: String,
    val timeCreated: Long,
    val absolutePath: String,
    val isDirectory: Boolean,
    val isFile: Boolean,
    val size: Long,
    val uri: Uri,
    val fileType: FileType,
    val bitmap: Bitmap? = null
): Parcelable