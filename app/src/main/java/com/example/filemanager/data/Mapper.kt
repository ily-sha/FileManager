package com.example.filemanager


import androidx.core.net.toUri
import com.example.filemanager.domain.FileEntity
import com.example.filemanager.domain.FileType
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes


fun mapFileIoListToFileEntities(files: Array<out File>): List<FileEntity> {

    fun defineFileType(file: File): FileType {
        if (file.isFile){
            return when (file.extension){
                "png" -> FileType.PNG
                "jpg" -> FileType.JPG
                "webp" -> FileType.WEBP
                "jpeg" -> FileType.JPEG
                "mp4" -> FileType.MP4
                "MOV" -> FileType.MOV
                else -> FileType.ELSE
            }
        }
        return FileType.ELSE


    }
    return files.map {
        FileEntity(
            name = it.name,
            absolutePath = it.absolutePath,
            isFile = it.isFile,
            isDirectory = it.isDirectory,
            timeCreated = Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).creationTime().toMillis(),
            size = if (it.isDirectory) 0 else it.length(),
            uri = it.toUri(),
            fileType = defineFileType(it)
//            drawable = Drawable.createFromPath(it.absolutePath)

        )
    }
}



//fun mapFileEntityToFileDbModel(fileEntity: FileEntity): FileDbModel {
//    return FileDbModel(
//        fileEntity.absolutePath,
//        computeHashCode(fileEntity.absolutePath)
//    )
//
//}




