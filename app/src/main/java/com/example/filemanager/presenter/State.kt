package com.example.filemanager.presenter

import java.io.File

sealed class State


object Loading : State()
class FileUploaded(val newFiles: List<File>, val changedFiles: List<File>): State()
object PermissionDenial : State()