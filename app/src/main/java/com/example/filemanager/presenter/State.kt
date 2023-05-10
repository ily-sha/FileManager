package com.example.filemanager.presenter

import java.io.File

sealed class State


object Loading : State()
class FileChanged(val filesList: List<File>): State()
object PermissionDenial : State()